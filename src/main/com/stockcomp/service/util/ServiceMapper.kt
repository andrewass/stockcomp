package com.stockcomp.service.util

import com.stockcomp.entity.contest.*
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto

fun mapToInvestmentDto(investment: Investment?, symbol: String) =
    InvestmentDto(
        symbol = symbol,
        amount = investment?.amount ?: 0
    )

fun mapToAwaitingOrder(
    participant: Participant, request: InvestmentTransactionRequest,
    transactionType: TransactionType
) =
    InvestmentOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        expirationTime = request.expirationTime,
        totalAmount = request.amount,
        transactionType = transactionType,
        participant = participant
    )