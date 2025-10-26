package com.stockcomp.leaderboard

import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.medal.MedalDto
import com.stockcomp.leaderboard.internal.medal.mapToMedalDto
import org.springframework.data.domain.Page

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

fun mapToLeaderboardEntryDto(src: LeaderboardEntry) =
    LeaderboardEntryDto(
        ranking = src.ranking,
        contestCount = src.contestCount,
        score = src.score,
        medals = src.medals.map { mapToMedalDto(it) },
    )

fun mapToLeaderboardEntryPageDto(pageEntry: Page<LeaderboardEntry>) =
    LeaderboardEntryPageDto(
        entries = pageEntry.get().map { mapToLeaderboardEntryDto(it) }.toList(),
        totalEntriesCount = pageEntry.totalElements,
    )
