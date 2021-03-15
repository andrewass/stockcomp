package com.stockcomp.request

class InvestmentTransactionRequest(
    val isBuying: Boolean,
    val username: String,
    val contestNumber: Int,
    val symbol: String,
    val amount: Int
)