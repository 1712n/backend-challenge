package com.inca.task.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient

//We do not expect any improvement in this class, but feel free to refactor it if you want
@Configuration
class WebClientConfig {

    @Bean
    fun httpClient(binanceApiProperties: BinanceApiProperties): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, binanceApiProperties.configuration.timeout.connection.toMillis().toInt())
            .doOnConnected { conn: Connection ->
                conn.addHandlerLast(ReadTimeoutHandler(binanceApiProperties.configuration.timeout.read.toSeconds().toInt()))
                    .addHandlerLast(WriteTimeoutHandler(binanceApiProperties.configuration.timeout.write.toSeconds().toInt()))
            }
    }

    @Bean
    fun webClient(binanceApiProperties: BinanceApiProperties, httpClient: HttpClient): WebClient {
        return WebClient.builder()
            .baseUrl(binanceApiProperties.baseUri.toString())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { configurer: ClientCodecConfigurer ->
                    configurer.defaultCodecs()
                        .maxInMemorySize(binanceApiProperties.configuration.maxMemorySize.toBytes().toInt())
                }
                .build()
            )
            .build()
    }
}