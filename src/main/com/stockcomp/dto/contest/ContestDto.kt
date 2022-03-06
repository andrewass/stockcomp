package com.stockcomp.dto.contest

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.LeaderboardUpdateStatus
import java.time.LocalDateTime

data class ContestDto(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestNumber: Int,
    val participantCount: Int,
    val contestStatus: ContestStatus,
    val leaderboardUpdateStatus: LeaderboardUpdateStatus
)