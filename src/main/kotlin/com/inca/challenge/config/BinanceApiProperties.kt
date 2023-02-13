package com.inca.challenge.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.util.unit.DataSize
import java.net.URL
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("binance.api")
data class BinanceApiProperties(
    val baseUrl: URL,
    val maxMemorySize: DataSize,
    val timeout: Timeout,
    val orderBook: OrderBook,
) {
    data class Timeout(
        val connection: Duration,
        val read: Duration,
        val write: Duration,
    )

    data class OrderBook(
        val depth: Int,
        val infoEndpointTimeout: Duration,
        val dryRun: Boolean
    )
}
