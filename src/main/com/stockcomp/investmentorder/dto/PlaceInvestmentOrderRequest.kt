package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.TransactionType
import java.time.LocalDateTime

data class PlaceInvestmentOrderRequest(
    val contestNumber: Int,
    val symbol: String,
    val amount: Int,
    val currency: String,
    val expirationTime: LocalDateTime,
    val acceptedPrice: Double,
    val transactionType: TransactionType
)