package com.stockcomp.investmentorder.dto

import com.stockcomp.domain.contest.enums.TransactionType
import java.time.LocalDateTime

data class InvestmentOrderRequest(
    val contestNumber: Int,
    val symbol: String,
    val amount: Int,
    val currency: String,
    val expirationTime: LocalDateTime,
    val acceptedPrice: Double,
    val transactionType: TransactionType
)