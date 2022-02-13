package com.stockcomp.dto.contest

import java.time.LocalDateTime

data class ContestDto(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val participantCount: Int,
    val contestStatus: String,
    val leaderboardUpdateStatus: String
)