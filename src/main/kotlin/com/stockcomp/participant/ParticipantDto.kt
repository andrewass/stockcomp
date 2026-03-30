package com.stockcomp.participant

import com.stockcomp.contest.ContestDto
import com.stockcomp.participant.internal.Participant
import com.stockcomp.participant.internal.investmentorder.OrderStatus
import com.stockcomp.user.UserDetailsDto
import jakarta.validation.constraints.Positive

data class CommonParticipantDto(
    val participantId: Long,
    val rank: Int? = null,
    val totalValue: Double,
    val username: String,
    val country: String?,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
)

data class UserParticipantDto(
    val participantId: Long,
    val userId: Long,
    val rank: Int? = null,
    val totalValue: Double,
    val totalInvestmentValue: Double,
    val remainingFunds: Double,
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

fun mapToHistoricParticipant(participant: Participant) =
    HistoricParticipantDto(
        participant = toUserParticipantDto(participant),
        investments = participant.investments().map { mapToInvestmentDto(it) },
    )

fun toDetailedParticipant(
    participant: Participant,
    symbol: String,
    contest: ContestDto,
) = DetailedParticipantDto(
    participant = toUserParticipantDto(participant),
    contest = contest,
    investments =
        participant
            .investments()
            .filter { it.symbol == symbol }
            .map { mapToInvestmentDto(it) },
    activeOrders =
        participant
            .investmentOrders()
            .filter { it.symbol == symbol }
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
    completedOrders =
        participant
            .investmentOrders()
            .filter { it.symbol == symbol }
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) },
)

fun toDetailedParticipant(
    participant: Participant,
    contest: ContestDto,
) = DetailedParticipantDto(
    participant = toUserParticipantDto(participant),
    contest = contest,
    investments = participant.investments().map { mapToInvestmentDto(it) },
    activeOrders =
        participant
            .investmentOrders()
            .filter { it.orderStatus == OrderStatus.ACTIVE }
            .map { mapToInvestmentOrderDto(it) },
    completedOrders =
        participant
            .investmentOrders()
            .filter { it.orderStatus == OrderStatus.COMPLETED }
            .map { mapToInvestmentOrderDto(it) },
)

fun toParticipantPage(
    participants: List<Participant>,
    userDetails: List<UserDetailsDto>,
    totalEntriesCount: Long,
) = CommonParticipantPageDto(
    participants =
        participants
            .map { participant ->
                toCommonParticipantDto(participant, userDetails.first { user -> user.userId == participant.userId })
            }.toList(),
    totalEntriesCount = totalEntriesCount,
)

fun toUserParticipantDto(participant: Participant): UserParticipantDto {
    val participantId = participant.participantId ?: throw NoSuchElementException("Participant has no id")
    return UserParticipantDto(
        participantId = participantId,
        userId = participant.userId,
        rank = participant.rank(),
        totalValue = participant.totalValue(),
        totalInvestmentValue = participant.totalInvestmentValue(),
        remainingFunds = participant.remainingFunds(),
    )
}

fun toCommonParticipantDto(
    participant: Participant,
    userDetails: UserDetailsDto,
): CommonParticipantDto {
    val participantId = participant.participantId ?: throw NoSuchElementException("Participant has no id")
    return CommonParticipantDto(
        rank = participant.rank(),
        totalValue = participant.totalValue(),
        totalInvestmentValue = participant.totalInvestmentValue(),
        remainingFunds = participant.remainingFunds(),
        participantId = participantId,
        username = userDetails.username,
        country = userDetails.country,
    )
}
