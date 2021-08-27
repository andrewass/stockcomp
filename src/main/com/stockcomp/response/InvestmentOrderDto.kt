package com.stockcomp.response

data class InvestmentOrderDto(
    val orderId: Long,
    val status: String,
    val remainingAmount: Int,
    val totalAmount: Int,
    val transactionType: String,
    val symbol: String,
    val acceptedPrice: Double,
    val currency: String
)