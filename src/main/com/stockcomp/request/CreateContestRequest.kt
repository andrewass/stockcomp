package com.stockcomp.request

import java.time.LocalDateTime

data class CreateContestRequest(
    val startTime: LocalDateTime,
    val contestNumber: Int
)