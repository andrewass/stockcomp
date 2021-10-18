package com.stockcomp.dto

data class TransactionDto(
    val symbol: String,
    val amount: Int,
    val currentPrice: Double
)