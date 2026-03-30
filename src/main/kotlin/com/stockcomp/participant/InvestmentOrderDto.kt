package com.stockcomp.participant

import com.stockcomp.participant.internal.investmentorder.InvestmentOrder
import com.stockcomp.participant.internal.investmentorder.OrderStatus
import com.stockcomp.participant.internal.investmentorder.TransactionType
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class PlaceInvestmentOrderRequest(
    @field:Positive
    val participantId: Long,
    @field:NotBlank
    val symbol: String,
    @field:Positive
    val amount: Int,
    @field:NotBlank
    @field:Size(min = 3, max = 3)
    val currency: String,
    @field:Future
    val expirationTime: LocalDateTime,
    @field:Positive
    val acceptedPrice: Double,
    val transactionType: TransactionType,
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
