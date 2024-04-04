package com.stockcomp.participant.dto

import com.stockcomp.investment.dto.InvestmentDto
import com.stockcomp.investmentorder.dto.InvestmentOrderDto

data class ParticipantDto(
    val displayName : String?,
    val rank: Int?,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
    val country: String?,
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
