package com.stockcomp.participant.participant

import com.stockcomp.contest.ContestDto
import com.stockcomp.participant.investment.InvestmentDto
import com.stockcomp.participant.investment.mapToInvestmentDto
import com.stockcomp.participant.investmentorder.InvestmentOrderDto
import com.stockcomp.participant.investmentorder.OrderStatus
import com.stockcomp.participant.investmentorder.mapToInvestmentOrderDto
import com.stockcomp.user.UserDetailsDto
import org.springframework.data.domain.Page

data class ParticipantDto(
    val participantId: Long,
    val rank: Int? = null,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
    val participantCount: Long? = null,
)

data class ParticipantWithUserDetailsDto(
    val participant: ParticipantDto,
    val userDetails: UserDetailsDto
)

data class ParticipantPageDto(
    val participants: List<ParticipantDto>,
    val totalEntriesCount: Long
)

data class ContestParticipantDto(
    val participant: ParticipantDto,
    val contest: ContestDto
)

data class HistoricParticipantDto(
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>
)

data class DetailedParticipantDto(
    val contest: ContestDto,
    val participant: ParticipantDto,
    val investments: List<InvestmentDto>,
    val activeOrders: List<InvestmentOrderDto>,
    val completedOrders: List<InvestmentOrderDto>
)


fun mapToHistoricParticipant(source: Participant) =
    HistoricParticipantDto(
        participant = toParticipantDto(source),
        investments = source.investments.map { mapToInvestmentDto(it) }
    )

fun toParticipantWithUserDetailsDto(participant: Participant, userDetails: UserDetailsDto) {
    ParticipantWithUserDetailsDto(
        participant = toParticipantDto(participant),
        userDetails = userDetails
    )
}

fun toParticipantDto(source: Participant, participantCount: Long? = null) =
    ParticipantDto(
        rank = source.rank,
        totalValue = source.totalValue,
        totalInvestmentValue = source.totalInvestmentValue,
        remainingFunds = source.remainingFunds,
        participantCount = participantCount,
        participantId = source.participantId!!
    )


fun toDetailedParticipant(
    source: Participant,
    symbol: String,
    participantCount: Long? = null,
    contest: ContestDto,
) =
    DetailedParticipantDto(
        participant = toParticipantDto(source, participantCount),
        contest = contest,
        investments = source.investments.filter { it.symbol == symbol }
            .map { mapToInvestmentDto(it) },
        activeOrders = source.investmentOrders.filter { it.symbol == symbol }
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
        completedOrders = source.investmentOrders.filter { it.symbol == symbol }
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) }
    )

fun toDetailedParticipant(
    source: Participant,
    participantCount: Long? = null,
    contest: ContestDto
) =
    DetailedParticipantDto(
        participant = toParticipantDto(source, participantCount),
        contest = contest,
        investments = source.investments.map { mapToInvestmentDto(it) },
        activeOrders = source.investmentOrders
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
        completedOrders = source.investmentOrders
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) }
    )

fun toParticipantPage(source: Page<Participant>) =
    ParticipantPageDto(
        participants = source.get().map { toParticipantDto(it) }.toList(),
        totalEntriesCount = source.totalElements
    )
