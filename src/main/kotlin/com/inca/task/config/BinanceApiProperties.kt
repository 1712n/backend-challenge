package com.inca.task.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.util.unit.DataSize
import java.net.URI
import java.net.URL
import java.time.Duration

//We do not expect any improvement in this class, but feel free to refactor it if you want
@ConstructorBinding
@ConfigurationProperties("binance.api")
data class BinanceApiProperties(
    val baseUri: URI,
    val configuration: Configuration
) {

    data class Configuration(
        val maxMemorySize: DataSize,
        val timeout: Timeout,
    ) {
        data class Timeout(
            val connection: Duration,
            val read: Duration,
            val write: Duration,
        )
    }
}
