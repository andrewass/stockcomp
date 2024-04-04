package com.stockcomp.participant.dto

import com.stockcomp.investment.dto.mapToInvestmentDto
import com.stockcomp.investmentorder.dto.mapToInvestmentOrderDto
import com.stockcomp.participant.entity.Participant
import org.springframework.data.domain.Page

fun mapToHistoricParticipant(source: Participant) =
    HistoricParticipantDto(
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

fun mapToDetailedParticipant(source: Participant, symbol: String) =
    DetailedParticipantDto(
        participant = mapToParticipantDto(source),
        investments = source.investments.filter { it.symbol == symbol }
            .map { mapToInvestmentDto(it) },
        activeOrders = source.investmentOrders.filter { it.symbol == symbol }
            .map { mapToInvestmentOrderDto(it) },
        completedOrders = source.investmentOrders.filter { it.symbol == symbol }
            .map { mapToInvestmentOrderDto(it) }
    )

fun mapToParticipantPage(source: Page<Participant>) =
    ParticipantPageDto(
        participants = source.get().map { mapToParticipantDto(it) }.toList(),
        totalEntriesCount = source.totalElements
    )