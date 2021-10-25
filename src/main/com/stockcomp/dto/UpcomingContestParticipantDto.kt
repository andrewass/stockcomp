package com.stockcomp.dto

import java.time.LocalDateTime

data class UpcomingContestParticipantDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val contestStatus: String,
    val userParticipating : Boolean,
    val rank : Int?,
    val participantCount: Int
)