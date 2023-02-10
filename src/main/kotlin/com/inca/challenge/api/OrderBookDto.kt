package com.inca.challenge.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal

data class OrderBookDto(
    val lastUpdateId: Long,
    val bids: List<Order>,
    val asks: List<Order>,
) {
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder("price", "volume")
    data class Order(val price: BigDecimal, val volume: BigDecimal)
}
