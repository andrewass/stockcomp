package com.stockcomp.contest.dto

data class RealTimePrice(
    val price: Double,
    val usdPrice: Double,
    val currency: String
)