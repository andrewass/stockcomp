package com.stockcomp.request

import java.time.LocalDateTime

data class InvestmentTransactionRequest(
    val contestNumber: Int,
    val symbol: String,
    val amount: Int,
    val expirationTime: LocalDateTime,
    val acceptedPrice: Double
)