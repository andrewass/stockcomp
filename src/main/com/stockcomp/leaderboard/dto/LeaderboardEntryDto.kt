package com.stockcomp.leaderboard.dto

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Int,
    val displayName: String?,
    val country: String?,
    val contestCount: Int,
    val medals: List<MedalDto>
)