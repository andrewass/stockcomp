package com.stockcomp.response.leaderboard

data class LeaderboardEntryDto(
    val ranking: Int,
    val contestCount: Int,
    val medals: List<MedalDto>
)