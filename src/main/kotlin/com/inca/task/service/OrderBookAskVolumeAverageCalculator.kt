package com.inca.task.service

import com.inca.task.config.OrderBookProperties
import com.inca.task.dto.ExchangeInfoBinanceResponse
import com.inca.task.dto.OrderBookBinanceResponse
import com.inca.task.dto.SpotSymbolBinanceResponse
import com.inca.task.dto.SpotSymbolBinanceResponse.SpotSymbol
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

@Service
class OrderBookAskVolumeAverageCalculator(
    private val orderBookProperties: OrderBookProperties,
    private val webClient: WebClient,
) : CommandLineRunner {

    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        val startTimestamp: Long = System.currentTimeMillis()
        val symbols: List<SpotSymbol> = callForSymbols()
        val weightLimits: WeightLimits = callForWeights()
        val orderBooks: Flux<Pair<SpotSymbol, OrderBookBinanceResponse>> =
            callForOrderBook(orderBookProperties.depth, symbols, weightLimits)
        val averageVolume = computeAverageVolume(orderBooks)
        log.info { "All data received in ${Duration.ofMillis(System.currentTimeMillis() - startTimestamp)}" }
        log.info { "Average volume = $averageVolume" }
    }

    //TODO need to optimize.
    //The problem here is that it takes a long time to retrieve all data..
    //We expect that this time can be reduced if you make these calls in parallel.
    //Be careful not to violate the binance api limits. See api doc: https://binance-docs.github.io/apidocs/spot/en/#order-book.
    //You can use weightLimits variable to track of the current available limits.
    private fun callForOrderBook(
        bookDepth: Int,
        symbolList: List<SpotSymbol>,
        weightLimits: WeightLimits
    ): Flux<Pair<SpotSymbol, OrderBookBinanceResponse>> {
        var receivedOrderBookCount = 0
        log.info { "Retrieving orderBooks..." }
        return Flux.fromIterable(symbolList)
            .publishOn(Schedulers.boundedElastic())
            .map { symbol ->
                val orderBookBinanceResponse: OrderBookBinanceResponse = webClient.get()
                    .uri("/depth?symbol=${symbol.symbol}&limit=${bookDepth}")
                    .retrieve()
                    .bodyToMono(OrderBookBinanceResponse::class.java)
                    .doOnNext { log.info { "${++receivedOrderBookCount} of ${symbolList.size}" } }
                    .block()!!
                Pair(symbol, orderBookBinanceResponse)
            }
            .doOnComplete { log.info { "Retrieved ${receivedOrderBookCount} orderBooks" } }
    }

    //TODO need to optimize.
    //The problem here is that the whole orderBookList is stored in memory to compute the average volume.
    //We expect that memory consumption can be reduced here.
    private fun computeAverageVolume(orderBooks: Flux<Pair<SpotSymbol, OrderBookBinanceResponse>>): Double {
        val orderBookList = orderBooks.collectList().block()!!

        val volumes = orderBookList.stream()
            .flatMap { (symbol, orderBook) -> orderBook.asks.stream() }
            .map { it.get(1).toDouble() }
            .toList()

        return volumes.sum() / volumes.size
    }

    //We do not expect any improvement in this method, but feel free to refactor it if you want
    private fun callForSymbols(): List<SpotSymbol> {
        return webClient.get()
            .uri("/exchangeInfo")
            .retrieve()
            .bodyToMono(SpotSymbolBinanceResponse::class.java)
            .doOnNext { log.info { "Retrieved ${it.symbols.size} symbols" } }
            .map { it.symbols }
            .block()!!
    }

    //We do not expect any improvement in this method, but feel free to refactor it if you want
    private fun callForWeights(): WeightLimits {
        return webClient.get()
            .uri("/exchangeInfo")
            .retrieve()
            .toEntity<ExchangeInfoBinanceResponse>()
            .map {
                val usedWeight = it.headers.getFirst("x-mbx-used-weight-1m")?.toInt()
                    ?: throw IllegalStateException("`x-mbx-used-weight-1m` header was not found")
                val totalWeight = it.body?.rateLimits?.find { it.rateLimitType == "REQUEST_WEIGHT" }?.limit
                    ?: throw IllegalStateException("REQUEST_WEIGHT value was not found")
                WeightLimits(usedWeight, totalWeight)
            }
            .block(Duration.ofSeconds(10))!!
    }

    private data class WeightLimits(
        val usedWeight: Int,
        val totalWeight: Int
    )

}