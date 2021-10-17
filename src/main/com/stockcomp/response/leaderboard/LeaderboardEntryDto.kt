package com.stockcomp.response.leaderboard

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Double,
    val username: String,
    val contestCount: Int,
    val medals: List<MedalDto>
)