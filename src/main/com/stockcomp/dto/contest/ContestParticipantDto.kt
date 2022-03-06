package com.stockcomp.dto.contest

data class ContestParticipantDto(
    private val contest: ContestDto,
    private var participant: ParticipantDto? = null
)