package com.stockcomp.response

data class InvestmentOrderDto(
    val orderId: Long,
    val status: String,
    val amount: Int,
    val transactionType: String,
    val symbol: String
)