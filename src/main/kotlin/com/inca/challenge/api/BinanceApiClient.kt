package com.inca.challenge.api

import com.inca.challenge.config.BinanceApiProperties
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono

/**
 * Service for interaction with Binance REST API
 */
@Service
class BinanceApiClient(
    private val binanceApiProperties: BinanceApiProperties,
    private val webClient: WebClient,
) {
    private val log = KotlinLogging.logger {}

    /**
     * Returns order book mono for specified symbol with specified in
     * [BinanceApiProperties.orderBook.depth] property depth limit
     */
    fun getSymbolOrderBookMono(symbol: ExchangeInfoDto.Symbol): Mono<OrderBookDto> =
        webClient.get()
            .uri("/depth?symbol=${symbol.symbol}&limit=${binanceApiProperties.orderBook.depth}")
            .retrieve()
            .bodyToMono(OrderBookDto::class.java)

    /**
     * Retrieve symbols dictionary. Symbol is an exchange pair, e.q BTC-ETH
     */
    fun getSymbols(): List<ExchangeInfoDto.Symbol> = webClient.get()
        .uri("/exchangeInfo")
        .retrieve()
        .bodyToMono(ExchangeInfoDto::class.java)
        .map { it.symbols }
        .map { if (binanceApiProperties.orderBook.dryRun) it.subList(0, 10) else it } // pick 10 symbols in dry-run mode
        .doOnNext { log.info { "Retrieved ${it.size} symbols" } }
        .block(binanceApiProperties.orderBook.infoEndpointTimeout)!!

    /**
     * Retrieve information about actual API rate limits, which can help you avoid
     * exceeding the maximum number of request attempts
     */
    fun getRateLimits(): ApiRateLimits = webClient.get()
        .uri("/exchangeInfo")
        .retrieve()
        .toEntity<ExchangeInfoDto>()
        .map { it.toRateLimits() }
        .block(binanceApiProperties.orderBook.infoEndpointTimeout)!!

    private fun ResponseEntity<ExchangeInfoDto>.toRateLimits(): ApiRateLimits {
        val usedWeight = this.headers
            .getFirst("x-mbx-used-weight-1m")
            ?.toInt()
            ?: throw IllegalStateException("`x-mbx-used-weight-1m` header not found")
        val totalWeight = this.body
            ?.rateLimits
            ?.find { it.rateLimitType == "REQUEST_WEIGHT" }
            ?.limit
            ?: throw IllegalStateException("REQUEST_WEIGHT value not found")
        return ApiRateLimits(usedWeight, totalWeight)
    }
}

data class ApiRateLimits(
    val usedWeight: Int,
    val totalWeight: Int
)