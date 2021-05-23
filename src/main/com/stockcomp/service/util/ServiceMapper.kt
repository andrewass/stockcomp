package com.stockcomp.service.util

import com.stockcomp.entity.contest.Investment
import com.stockcomp.entity.contest.InvestmentOrder
import com.stockcomp.entity.contest.Participant
import com.stockcomp.entity.contest.TransactionType
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto

fun mapToInvestmentDto(investment: Investment?, symbol: String) =
    InvestmentDto(
        symbol = symbol,
        amount = investment?.amount ?: 0
    )

fun mapToInvestmentOrder(
    participant: Participant, request: InvestmentTransactionRequest, transactionType: TransactionType
) =
    InvestmentOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        expirationTime = request.expirationTime,
        totalAmount = request.amount,
        transactionType = transactionType,
        participant = participant
    )