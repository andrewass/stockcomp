package com.stockcomp.participant.dto

import com.stockcomp.participant.entity.Participant

fun mapToParticipantDto(participant: Participant) =
    ParticipantDto(
        username = participant.user.username,
        rank = participant.rank,
        totalValue = participant.totalValue,
        totalInvestmentValue = participant.totalInvestmentValue,
        remainingFunds = participant.remainingFunds,
        country = participant.user.country,
        startTime = participant.contest.startTime,
        contestNumber = participant.contest.contestNumber
    )