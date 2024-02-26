package com.stockcomp.participant.dto

import com.stockcomp.investment.dto.InvestmentDto
import com.stockcomp.investment.entity.Investment
import com.stockcomp.participant.entity.Participant
import org.springframework.data.domain.Page

fun mapToDetailedParticipant(source: Participant) =
    DetailedParticipantDto(
        participant = mapToParticipantDto(source),
        investments = source.investments.map { mapToInvestmentDto(it) }
    )


fun mapToParticipantDto(source: Participant) =
    ParticipantDto(
        displayName = source.user.username,
        rank = source.rank,
        totalValue = source.totalValue,
        totalInvestmentValue = source.totalInvestmentValue,
        remainingFunds = source.remainingFunds,
        country = source.user.country,
        contestNumber = source.contest.contestNumber
    )


fun mapToParticipantPageDto(source: Page<Participant>) =
    ParticipantPageDto(
        participants = source.get().map { mapToParticipantDto(it) }.toList(),
        totalEntriesCount = source.totalElements
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