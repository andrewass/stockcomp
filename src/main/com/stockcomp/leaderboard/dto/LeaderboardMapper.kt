package com.stockcomp.leaderboard.dto

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal

fun mapToMedalDto(src : Medal) =
    MedalDto(
        medalValue = src.medalValue.decode,
        position = src.position
    )

fun mapToLeaderboardEntryDto(src : LeaderboardEntry) =
    LeaderboardEntryDto(
        ranking = src.ranking,
        contestCount = src.contestCount,
        score = src.score,
        username = src.user.username,
        country = src.user.country,
        medals = src.medals.map { mapToMedalDto(it) }
    )