package com.stockcomp.contest.dto

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.entity.LeaderboardUpdateStatus
import java.time.LocalDateTime

data class ContestDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val participantCount: Int,
    val contestStatus: ContestStatus,
    val leaderboardUpdateStatus: LeaderboardUpdateStatus
)