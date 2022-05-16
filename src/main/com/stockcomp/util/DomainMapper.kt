package com.stockcomp.util

import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.dto.MedalDto
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.entity.User


fun User.toUserDetailsDto() =
    UserDetailsDto(
        id = this.id,
        username = this.username,
        country = this.country,
        fullName = this.fullName,
        userRole = this.userRole.name
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

fun mapToInvestmentOrder(participant: Participant, request: PlaceInvestmentOrderRequest) =
    InvestmentOrder(
        symbol = request.symbol,
        acceptedPrice = request.acceptedPrice,
        currency = request.currency,
        expirationTime = request.expirationTime,
        totalAmount = request.amount,
        transactionType = request.transactionType,
        participant = participant
    )

