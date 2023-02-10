package com.inca.challenge.api

data class ExchangeInfoDto(
    val rateLimits: List<RateLimit>,
    val symbols: List<Symbol>,
) {
    data class RateLimit(
        val rateLimitType: String,
        val interval: String,
        val intervalNum: Int,
        val limit: Int
    )

    data class Symbol(
        val symbol: String,
        val baseAsset: String,
        val quoteAsset: String,
        val status: String
    )
}
