package com.stockcomp.service.util

import com.stockcomp.entity.contest.Investment
import com.stockcomp.entity.contest.Transaction
import com.stockcomp.response.InvestmentDto
import com.stockcomp.response.TransactionDto

fun mapToInvestmentDto(investment: Investment?, symbol: String) =
    InvestmentDto(
        symbol = symbol,
        amount = investment?.amount ?: 0
    )

fun mapToTransactionDto(transaction: Transaction) =
    TransactionDto(
        symbol = transaction.symbol,
        amount = transaction.amount,
        currentPrice = transaction.currentPrice
    )
