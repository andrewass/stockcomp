package com.stockcomp.participant.internal

import com.stockcomp.participant.CommonParticipantDto
import com.stockcomp.participant.CommonParticipantPageDto
import com.stockcomp.participant.HistoricParticipantDto
import com.stockcomp.participant.UserParticipantDto
import com.stockcomp.participant.internal.investment.mapToInvestmentDto
import com.stockcomp.user.UserDetailsDto

fun mapToHistoricParticipant(participant: Participant) =
    HistoricParticipantDto(
        participant = toUserParticipantDto(participant),
        investments = participant.investments().map { mapToInvestmentDto(it) },
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
