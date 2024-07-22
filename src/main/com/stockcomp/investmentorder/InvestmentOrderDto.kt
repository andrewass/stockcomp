package com.stockcomp.investmentorder

import com.stockcomp.investmentorder.internal.InvestmentOrder
import java.time.LocalDateTime

data class InvestmentOrderDto(
    val orderId: Long?,
    val symbol: String,
    val totalAmount: Int,
    val remainingAmount: Int,
    val acceptedPrice: Double,
    val currency: String,
    val expirationTime: LocalDateTime,
    val transactionType: TransactionType,
    val orderStatus: OrderStatus
)

fun mapToInvestmentOrderDto(source : InvestmentOrder) =
    InvestmentOrderDto(
        orderId = source.orderId,
        symbol = source.symbol,
        acceptedPrice = source.acceptedPrice,
        currency = source.currency,
        expirationTime = source.expirationTime,
        orderStatus = source.orderStatus,
        remainingAmount = source.remainingAmount,
        totalAmount = source.totalAmount,
        transactionType = source.transactionType
    )