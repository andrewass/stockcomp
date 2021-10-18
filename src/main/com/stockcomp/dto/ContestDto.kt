package com.stockcomp.dto

import java.time.LocalDateTime

data class ContestDto(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val participantCount: Int,
    val running: Boolean,
    val completed: Boolean,
    val leaderboardUpdateStatus: String
)
