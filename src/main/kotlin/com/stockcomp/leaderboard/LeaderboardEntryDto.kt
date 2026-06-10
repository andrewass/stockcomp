package com.stockcomp.leaderboard

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Int,
    val contestCount: Int,
    val medals: List<MedalDto>,
)

data class LeaderboardEntryPageDto(
    val entries: List<LeaderboardEntryDto>,
    val totalEntriesCount: Long,
)
