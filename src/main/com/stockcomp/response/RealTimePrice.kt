package com.stockcomp.response

import java.time.LocalDateTime

data class RealTimePrice(
    val currentPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val previousClosePrice: Double,
    val time: LocalDateTime
)