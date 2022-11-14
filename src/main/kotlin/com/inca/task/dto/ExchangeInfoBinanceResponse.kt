package com.inca.task.dto

//We do not expect any improvement in this class, but feel free to refactor it if you want
data class ExchangeInfoBinanceResponse(
    val rateLimits: List<RateLimit>
) {
    data class RateLimit(
        val rateLimitType: String,
        val interval: String,
        val intervalNum: Int,
        val limit: Int
    )
}