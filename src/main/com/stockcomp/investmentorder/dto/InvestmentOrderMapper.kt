package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.InvestmentOrder

fun mapToInvestmentOrderDto(source : InvestmentOrder) =
    InvestmentOrderDto(
        symbol = source.symbol,
        acceptedPrice = source.acceptedPrice,
        currency = source.currency,
        expirationTime = source.expirationTime,
        orderStatus = source.orderStatus,
        remainingAmount = source.remainingAmount,
        totalAmount = source.totalAmount,
        transactionType = source.transactionType
    )