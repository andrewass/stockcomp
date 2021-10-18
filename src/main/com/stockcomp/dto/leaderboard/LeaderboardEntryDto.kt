package com.stockcomp.dto.leaderboard

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Double,
    val username: String,
    val country: String?,
    val contestCount: Int,
    val medals: List<MedalDto>
)