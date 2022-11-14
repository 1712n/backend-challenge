package com.inca.task.dto

//We do not expect any improvement in this class, but feel free to refactor it if you want
data class SpotSymbolBinanceResponse(
    val symbols: List<SpotSymbol>
) {
    data class SpotSymbol(
        val symbol: String,
        val baseAsset: String,
        val quoteAsset: String,
        val status: String
    )
}