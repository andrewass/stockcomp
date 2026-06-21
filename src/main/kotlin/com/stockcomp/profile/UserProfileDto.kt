package com.stockcomp.profile

import java.math.BigDecimal
import java.time.LocalDateTime

data class UserProfileDto(
    val userId: Long,
    val username: String,
    val fullName: String?,
    val country: String?,
    val performance: UserPerformanceSummaryDto,
    val leaderboard: LeaderboardStandingDto,
    val contestHistory: ContestHistoryPageDto,
)

data class UserPerformanceSummaryDto(
    val completedContests: Int,
    val wins: Int,
    val podiums: Int,
    val averageRank: BigDecimal,
    val averageReturnPercentage: BigDecimal,
)

data class LeaderboardStandingDto(
    val position: Int?,
    val score: Int,
    val goldMedals: Int,
    val silverMedals: Int,
    val bronzeMedals: Int,
)

data class ContestHistoryPageDto(
    val entries: List<ContestPerformanceDto>,
    val totalEntriesCount: Long,
)

data class ContestPerformanceDto(
    val contestId: Long,
    val contestName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val rank: Int,
    val finalPortfolioValue: BigDecimal,
    val gainLoss: BigDecimal,
    val returnPercentage: BigDecimal,
)
