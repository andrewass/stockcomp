package com.stockcomp.leaderboard.dto

data class LeaderboardEntryPageDto(
    val entries: List<LeaderboardEntryDto>,
    val totalEntriesCount: Long
)