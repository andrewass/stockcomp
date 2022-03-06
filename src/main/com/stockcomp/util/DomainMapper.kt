package com.stockcomp.util

import com.stockcomp.domain.contest.*
import com.stockcomp.domain.contest.enums.TransactionType
import com.stockcomp.domain.leaderboard.LeaderboardEntry
import com.stockcomp.domain.leaderboard.Medal
import com.stockcomp.domain.user.User
import com.stockcomp.dto.contest.*
import com.stockcomp.dto.leaderboard.LeaderboardEntryDto
import com.stockcomp.dto.leaderboard.MedalDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.request.InvestmentOrderRequest

fun Investment.toInvestmentDto() =
    InvestmentDto(
        id = this.id!!,
        symbol = this.symbol,
        totalProfit = this.totalProfit,
        totalValue = this.totalValue,
        amount = this.amount,
        averageUnitCost = this.averageUnitCost
    )

fun User.toUserDetailsDto() =
    UserDetailsDto(
        id = this.id,
        username = this.username,
        country = this.country,
        fullName = this.fullName,
        userRole = this.userRole.name
    )

fun Contest.toContestDto() =
    ContestDto(
        id = this.id!!,
        contestNumber = this.contestNumber,
        participantCount = this.participantCount,
        contestStatus = this.contestStatus,
        leaderboardUpdateStatus = this.leaderboardUpdateStatus,
        startTime = this.startTime,
        endTime = this.endTime
    )

fun Participant.toParticipantDto() =
    ParticipantDto(
        username = this.user.username,
        rank = this.rank,
        totalValue = this.totalValue,
        country = this.user.country,
        startTime = this.contest.startTime,
        contestNumber = this.contest.contestNumber
    )

fun Medal.toMedalDto() =
    MedalDto(
        medalValue = this.medalValue.decode,
        position = this.position
    )

fun LeaderboardEntry.toLeaderboardEntryDto() =
    LeaderboardEntryDto(
        ranking = this.ranking,
        contestCount = this.contestCount,
        score = this.score,
        username = this.user.username,
        country = this.user.country,
        medals = this.medals.map { it.toMedalDto() }
    )

fun mapToInvestmentOrder(
    participant: Participant, request: InvestmentOrderRequest, transactionType: TransactionType
) =
    InvestmentOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        currency = request.currency,
        expirationTime = request.expirationTime,
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
        status = order.orderStatus.decode,
        transactionType = order.transactionType.decode,
        acceptedPrice = order.acceptedPrice,
        currency = order.currency
    )