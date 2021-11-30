package com.stockcomp.dto.leaderboard

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Int,
    val username: String,
    val country: String?,
    val contestCount: Int,
    val medals: List<MedalDto>
)