package com.stockcomp.contest.controller

import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.contest.domain.LeaderboardUpdateStatus
import java.time.LocalDateTime

data class ContestDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val contestStatus: ContestStatus,
    val leaderboardUpdateStatus: LeaderboardUpdateStatus
)

data class ContestPageDto(
    val contests: List<ContestDto>,
    val totalEntriesCount: Long
)
