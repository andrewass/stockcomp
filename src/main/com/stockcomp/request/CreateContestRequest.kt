package com.stockcomp.request

import java.time.LocalDateTime

data class CreateContestRequest(
    val contestNumber: Int,
    val startTime: LocalDateTime
)