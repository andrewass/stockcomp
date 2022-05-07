package com.stockcomp.participant.dto

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant

data class ContestParticipantDto(
    private val contest: Contest,
    private var participant: Participant? = null
)