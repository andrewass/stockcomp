package com.stockcomp.participant.dto

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
