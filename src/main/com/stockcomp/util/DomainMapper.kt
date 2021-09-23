package com.stockcomp.service.util

import com.stockcomp.domain.contest.*
import com.stockcomp.domain.user.User
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.response.*

fun Investment.toInvestmentDto() =
    InvestmentDto(
        id = this.id!!,
        symbol = this.symbol,
        totalProfit = this.totalProfit,
        totalValue = this.totalValue,
        amount = this.amount,
        averageUnitCost = this.averageUnitCost
    )

fun User.toUserDto() =
    UserDto(
        id = this.id!!,
        username = this.username,
        email =  this.email,
        userRole = this.userRole.name
    )

fun Contest.toContestDto() =
    ContestDto(
        id = this.id!!,
        contestNumber = this.contestNumber,
        running = this.running,
        completed = this.completed,
        startTime = this.startTime
    )

fun Participant.toParticipantDto() =
    ParticipantDto(
        username = this.user.username,
        rank = this.rank,
        totalValue = this.totalValue
    )

fun mapToInvestmentOrder(
    participant: Participant, request: InvestmentOrderRequest, transactionType: TransactionType
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