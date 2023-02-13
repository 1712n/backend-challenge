package com.inca.challenge.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun httpClient(binanceApiProperties: BinanceApiProperties): HttpClient {
        return HttpClient.create()
            .proxyWithSystemProperties()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                binanceApiProperties.timeout.connection.toMillis().toInt()
            )
            .doOnConnected { connection ->
                connection
                    .addHandlerLast(ReadTimeoutHandler(binanceApiProperties.timeout.read.toSeconds().toInt()))
                    .addHandlerLast(WriteTimeoutHandler(binanceApiProperties.timeout.write.toSeconds().toInt()))
            }
    }

    @Bean
    fun webClient(binanceApiProperties: BinanceApiProperties, httpClient: HttpClient): WebClient {
        return WebClient.builder()
            .baseUrl(binanceApiProperties.baseUrl.toString())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { configurer ->
                        configurer.defaultCodecs().maxInMemorySize(binanceApiProperties.maxMemorySize.toBytes().toInt())
                    }
                    .build()
            )
            .build()
    }
}