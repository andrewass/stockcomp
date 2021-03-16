package com.stockcomp.request

import com.stockcomp.entity.contest.TransactionType

class InvestmentTransactionRequest(
    val transactionType: TransactionType,
    val username: String,
    val contestNumber: Int,
    val symbol: String,
    val amount: Int
)