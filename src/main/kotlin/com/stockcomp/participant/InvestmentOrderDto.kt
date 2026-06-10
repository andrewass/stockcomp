package com.stockcomp.participant

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
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
    val acceptedPrice: BigDecimal,
    val transactionType: TransactionType,
)

data class InvestmentOrderDto(
    val orderId: Long?,
    val symbol: String,
    val totalAmount: Int,
    val remainingAmount: Int,
    val acceptedPrice: BigDecimal,
    val currency: String,
    val expirationTime: LocalDateTime,
    val transactionType: TransactionType,
    val orderStatus: OrderStatus,
)
