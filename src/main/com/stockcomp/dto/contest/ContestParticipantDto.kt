package com.stockcomp.dto.contest

import com.stockcomp.domain.contest.Contest

data class ContestParticipantDto(
    private val contest: Contest,
    private var participant: ParticipantDto? = null
)