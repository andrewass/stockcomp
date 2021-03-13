package com.stockcomp.request

class InvestmentTransactionRequest(
    val username: String,
    val contestNumber: Int,
    val investment: String,
    val amount: Int
)