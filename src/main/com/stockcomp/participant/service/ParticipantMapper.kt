package com.stockcomp.participant.service

import com.stockcomp.participant.dto.ParticipantDto

fun mapToParticipantDto(participant: com.stockcomp.participant.entity.Participant) =
    ParticipantDto(
        username = participant.user.username,
        rank = participant.rank,
        totalValue = participant.totalValue,
        remainingFunds = participant.remainingFunds,
        country = participant.user.country,
        startTime = participant.contest.startTime,
        contestNumber = participant.contest.contestNumber
    )