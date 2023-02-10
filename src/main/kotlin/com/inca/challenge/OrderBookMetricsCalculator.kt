package com.inca.challenge

import com.inca.challenge.api.ApiRateLimits
import com.inca.challenge.api.BinanceApiClient
import com.inca.challenge.api.ExchangeInfoDto.Symbol
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@Service
class OrderBookMetricsCalculator(
    private val binanceApiClient: BinanceApiClient,
) : CommandLineRunner {
    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        val start = Instant.now()

        val symbols: List<Symbol> = binanceApiClient.getSymbols()
        val rateLimits: ApiRateLimits = binanceApiClient.getRateLimits()

        var count = 0
        Flux.fromIterable(symbols)
            .concatMap { symbol -> binanceApiClient.getSymbolOrderBookMono(symbol) }
            .doOnNext { log.info { "Retrieved ${++count}/${symbols.size}" } }
            .doOnSubscribe { log.info { "Retrieving order books..." } }
            .doOnComplete { log.info { "Successfully retrieved: $count" } }
            .collectList()
            .map { orderBooks ->
                val asksVolumes = orderBooks.stream()
                    .flatMap { it.asks.stream() }
                    .toList()
                val bidsVolumes = orderBooks.stream()
                    .flatMap { it.bids.stream() }
                    .toList()
                VolumeMetrics(
                    asksVolumes.sumOf { it.volume } / BigDecimal(asksVolumes.size),
                    bidsVolumes.sumOf { it.volume } / BigDecimal(bidsVolumes.size)
                )
            }
            .doOnSuccess { log.info { "Volume metrics: $it" } }
            .doOnSuccess { log.info { "Volume metrics done for ${Duration.between(start, Instant.now()).seconds}s" } }
            .block(Duration.ofMinutes(20))!!
    }

    data class VolumeMetrics(val asksAvg: BigDecimal, val bidsAvg: BigDecimal)
}
