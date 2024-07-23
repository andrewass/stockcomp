package com.stockcomp.participant.presentation

import com.stockcomp.participant.investment.InvestmentDto
import com.stockcomp.participant.investmentorder.InvestmentOrderDto

data class ParticipantDto(
    val displayName: String? = null,
    val rank: Int? = null,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
    val country: String? = null,
    val contestNumber: Int
)

data class ParticipantPageDto(
    val participants: List<ParticipantDto>,
    val totalEntriesCount: Long
)

data class HistoricParticipantDto(
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>
)

data class DetailedParticipantDto(
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>,
    val activeOrders: List<InvestmentOrderDto>,
    val completedOrders: List<InvestmentOrderDto>
)


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
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
        completedOrders = source.investmentOrders.filter { it.symbol == symbol }
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) }
    )

fun toDetailedParticipant(source: Participant) =
    DetailedParticipantDto(
        participant = mapToParticipantDto(source),
        investments = source.investments.map { mapToInvestmentDto(it) },
        activeOrders = source.investmentOrders
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
        completedOrders = source.investmentOrders
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) }
    )

fun mapToParticipantPage(source: Page<Participant>) =
    ParticipantPageDto(
        participants = source.get().map { mapToParticipantDto(it) }.toList(),
        totalEntriesCount = source.totalElements
    )