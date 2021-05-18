package com.stockcomp.response

data class TransactionDto(
    val symbol: String,
    val amount: Int,
    val currentPrice: Double
)