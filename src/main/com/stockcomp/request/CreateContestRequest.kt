package com.stockcomp.request

import java.time.LocalDateTime

class CreateContestRequest(
    val startTime: LocalDateTime,
    val contestNumber: Int
)