package com.stockcomp.symbol.dto

data class CurrentPriceSymbolDto(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val previousClose: Double,
    val currency: String,
    val percentageChange: Double,
    val usdPrice: Double,
)
