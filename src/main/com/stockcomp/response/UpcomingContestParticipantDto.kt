package com.stockcomp.response

import java.time.LocalDateTime

data class UpcomingContestParticipantDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val running: Boolean,
    val userParticipating : Boolean,
    val rank : Int?,
    val participantCount: Int
)