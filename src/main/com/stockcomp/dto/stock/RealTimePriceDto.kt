package com.stockcomp.dto.stock

data class RealTimePriceDto(
    val price: Double,
    val openPrice: Double,
    val previousClose: Double,
    val dayLow: Double,
    val dayHigh: Double,
    val usdPrice: Double,
    val currency : String
)