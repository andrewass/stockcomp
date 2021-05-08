package com.stockcomp.request

data class InvestmentTransactionRequest(
    val contestNumber: Int,
    val symbol: String,
    val amount: Int
)