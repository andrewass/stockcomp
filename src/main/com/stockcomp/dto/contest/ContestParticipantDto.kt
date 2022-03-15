package com.stockcomp.dto.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant

data class ContestParticipantDto(
    private val contest: Contest,
    private var participant: Participant? = null
)