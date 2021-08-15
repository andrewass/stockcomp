package com.stockcomp.response

import java.time.LocalDateTime

data class UpcomingContest(
    val startTime: LocalDateTime,

    val contestNumber: Int,

    val isRunning: Boolean,

    val userIsParticipating : Boolean?
)