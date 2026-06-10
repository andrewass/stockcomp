package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.medal.mapToMedalDto
import org.springframework.data.domain.Page

fun mapToLeaderboardEntryDto(src: LeaderboardEntry) =
    LeaderboardEntryDto(
        ranking = src.ranking(),
        contestCount = src.contestCount(),
        score = src.score(),
        medals = src.medals.map { mapToMedalDto(it) },
    )

fun mapToLeaderboardEntryPageDto(pageEntry: Page<LeaderboardEntry>) =
    LeaderboardEntryPageDto(
        entries = pageEntry.get().map { mapToLeaderboardEntryDto(it) }.toList(),
        totalEntriesCount = pageEntry.totalElements,
    )
