package com.stockcomp.contest.dto

data class ContestParticipationDto(
    val contest: ContestDto,
    val participating: Boolean = false
)