package com.stockcomp.participant

import com.stockcomp.participant.internal.investmentorder.InvestmentOrder
import com.stockcomp.participant.internal.investmentorder.OrderStatus
import com.stockcomp.participant.internal.investmentorder.TransactionType
import java.time.LocalDateTime

data class PlaceInvestmentOrderRequest(
    val participantId: Long,
    val symbol: String,
    val amount: Int,
    val currency: String,
    val expirationTime: LocalDateTime,
    val acceptedPrice: Double,
    val transactionType: String,
)

data class InvestmentOrderDto(
    val orderId: Long?,
    val symbol: String,
    val totalAmount: Int,
    val remainingAmount: Int,
    val acceptedPrice: Double,
    val currency: String,
    val expirationTime: LocalDateTime,
    val transactionType: TransactionType,
    val orderStatus: OrderStatus,
)

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
