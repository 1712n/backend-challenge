package com.inca.task.dto

//We do not expect any improvement in this class, but feel free to refactor it if you want
data class OrderBookBinanceResponse(
    val lastUpdateId: Long,
    val bids: List<List<String>>,
    val asks: List<List<String>>
)