package com.stockcomp.participant

import com.stockcomp.contest.ContestDto
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CommonParticipantDto(
    val participantId: Long,
    val rank: Int? = null,
    val totalValue: BigDecimal,
    val username: String,
    val country: String?,
    val totalInvestmentValue: BigDecimal,
    val remainingFunds: BigDecimal,
)

data class UserParticipantDto(
    val participantId: Long,
    val userId: Long,
    val rank: Int? = null,
    val totalValue: BigDecimal,
    val totalInvestmentValue: BigDecimal,
    val remainingFunds: BigDecimal,
)

data class CommonParticipantPageDto(
    val participants: List<CommonParticipantDto>,
    val totalEntriesCount: Long,
)

data class ContestParticipantDto(
    val participant: UserParticipantDto,
    val contest: ContestDto,
)

data class HistoricParticipantDto(
    val participant: UserParticipantDto,
    val investments: List<InvestmentDto>,
)

data class DetailedParticipantDto(
    val contest: ContestDto,
    val participant: UserParticipantDto,
    val investments: List<InvestmentDto>,
    val activeOrders: List<InvestmentOrderDto>,
    val completedOrders: List<InvestmentOrderDto>,
)

data class SignUpParticipantRequest(
    @field:Positive
    val contestId: Long,
)
