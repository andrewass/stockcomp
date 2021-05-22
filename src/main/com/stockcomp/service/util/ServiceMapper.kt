package com.stockcomp.service.util

import com.stockcomp.entity.contest.*
import com.stockcomp.request.InvestmentTransactionRequest
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

fun mapToAwaitingOrder(
    participant: Participant, request: InvestmentTransactionRequest,
    transactionType: TransactionType
) =
    AwaitingOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        expirationTime = request.expirationTime,
        totalAmount = request.amount,
        transactionType = transactionType,
        participant = participant
    )