package com.stockcomp.investmentorder.dto

import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.entity.TransactionType
import java.time.LocalDateTime

data class InvestmentOrderDto(
    val orderId : Long?,
    val symbol: String,
    val totalAmount: Int,
    val remainingAmount: Int,
    val acceptedPrice: Double,
    val currency : String,
    val expirationTime: LocalDateTime,
    val transactionType: TransactionType,
    val orderStatus: OrderStatus
)
