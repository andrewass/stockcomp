package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.InvestmentOrderDto

fun mapToInvestmentOrderDto(source: InvestmentOrder) =
    InvestmentOrderDto(
        orderId = source.orderId,
        symbol = source.symbol,
        acceptedPrice = source.acceptedPrice,
        currency = source.currency,
        expirationTime = source.expirationTime,
        orderStatus = source.orderStatus,
        remainingAmount = source.remainingAmount,
        totalAmount = source.totalAmount,
        transactionType = source.transactionType,
    )
