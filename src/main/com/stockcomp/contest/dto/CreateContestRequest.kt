package com.stockcomp.contest.dto

import java.time.LocalDateTime

data class CreateContestRequest(
    val contestNumber: Int,
    val startTime: LocalDateTime
)