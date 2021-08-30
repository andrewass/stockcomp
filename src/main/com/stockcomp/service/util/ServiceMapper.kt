package com.stockcomp.service.util

import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto
import com.stockcomp.response.InvestmentOrderDto

fun mapToInvestmentDto(investment: Investment) =
    InvestmentDto(
        symbol = investment.symbol,
        totalProfit = investment.totalProfit,
        totalValue = investment.totalValue,
        amount = investment.amount,
        averageUnitCost = investment.averageUnitCost,
        name = investment.symbol
    )

fun mapToInvestmentOrder(
    participant: Participant, request: InvestmentTransactionRequest, transactionType: TransactionType
) =
    InvestmentOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        currency = request.currency,
        expirationTime = request.expirationTime.atStartOfDay(),
        totalAmount = request.amount,
        transactionType = transactionType,
        participant = participant
    )

fun mapToInvestmentOrderDto(order: InvestmentOrder) =
    InvestmentOrderDto(
        orderId = order.id!!,
        symbol = order.symbol,
        totalAmount = order.totalAmount,
        remainingAmount = order.remainingAmount,
        status= order.orderStatus.decode,
        transactionType = order.transactionType.decode,
        acceptedPrice = order.acceptedPrice,
        currency = order.currency
    )