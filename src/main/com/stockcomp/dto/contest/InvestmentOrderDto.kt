package com.stockcomp.dto.contest

import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.domain.contest.enums.TransactionType

data class InvestmentOrderDto(
    val orderId: Long,
    val orderStatus: OrderStatus,
    val remainingAmount: Int,
    val totalAmount: Int,
    val transactionType: TransactionType,
    val symbol: String,
    val acceptedPrice: Double,
    val currency: String
)