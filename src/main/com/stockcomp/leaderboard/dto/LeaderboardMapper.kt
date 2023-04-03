package com.stockcomp.leaderboard.dto

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal
import org.springframework.data.domain.Page

fun mapToMedalDto(src: Medal) =
    MedalDto(
        medalValue = src.medalValue.decode,
        position = src.position
    )

fun mapToLeaderboardEntryDto(src: LeaderboardEntry) =
    LeaderboardEntryDto(
        ranking = src.ranking,
        contestCount = src.contestCount,
        score = src.score,
        displayName = src.user.username,
        country = src.user.country,
        medals = src.medals.map { mapToMedalDto(it) }
    )

fun mapToLeaderboardEntryPageDto(pageEntry: Page<LeaderboardEntry>, currentPage: Int) =
    LeaderboardEntryPageDto(
        entries = pageEntry.get().map { mapToLeaderboardEntryDto(it) }.toList(),
        totalEntriesCount = pageEntry.totalElements
    )