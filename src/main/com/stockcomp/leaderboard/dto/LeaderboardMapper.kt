package com.stockcomp.leaderboard.dto

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal

fun Medal.toMedalDto() =
    MedalDto(
        medalValue = this.medalValue.decode,
        position = this.position
    )

fun LeaderboardEntry.toLeaderboardEntryDto() =
    LeaderboardEntryDto(
        ranking = this.ranking,
        contestCount = this.contestCount,
        score = this.score,
        username = this.user.username,
        country = this.user.country,
        medals = this.medals.map { it.toMedalDto() }
    )