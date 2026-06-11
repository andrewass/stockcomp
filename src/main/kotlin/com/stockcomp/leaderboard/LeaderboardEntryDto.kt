package com.stockcomp.leaderboard

import com.stockcomp.user.UserDetailsDto

data class LeaderboardEntryDto(
    val ranking: Int,
    val score: Int,
    val contestCount: Int,
    val medals: List<MedalDto>,
    val userDetails: UserDetailsDto,
)

data class LeaderboardEntryPageDto(
    val entries: List<LeaderboardEntryDto>,
    val totalEntriesCount: Long,
)
