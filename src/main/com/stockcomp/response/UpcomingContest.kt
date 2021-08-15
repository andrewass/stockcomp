package com.stockcomp.response

import java.time.LocalDateTime

data class UpcomingContest(
    val startTime: LocalDateTime,

    val contestNumber: Int,

    val running: Boolean,

    val userParticipating : Boolean
)