package com.stockcomp.participant.dto

import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.entity.Participant

fun mapToParticipantDto(source: Participant) =
    ParticipantDto(
        displayName = source.user.fullName,
        rank = source.rank,
        totalValue = source.totalValue,
        totalInvestmentValue = source.totalInvestmentValue,
        remainingFunds = source.remainingFunds,
        country = source.user.country,
        startTime = source.contest.startTime,
        contestNumber = source.contest.contestNumber
    )

fun mapToInvestmentDto(source: Investment) =
    InvestmentDto(
        amount = source.amount,
        averageUnitCost = source.averageUnitCost,
        symbol = source.symbol,
        totalProfit = source.totalProfit,
        totalValue = source.totalValue,
        contestNumber = source.participant.contest.contestNumber
    )