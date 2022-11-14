package com.inca.task.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.URI

//We do not expect any improvement in this class, but feel free to refactor it if you want
@ConstructorBinding
@ConfigurationProperties("order-book")
data class OrderBookProperties(
    val depth: Int,
)