package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.medal.mapToMedalDto
import com.stockcomp.user.UserDetailsDto
import org.springframework.data.domain.Page

fun mapToLeaderboardEntryDto(
    src: LeaderboardEntry,
    userDetails: UserDetailsDto,
) = LeaderboardEntryDto(
    ranking = src.ranking(),
    contestCount = src.contestCount(),
    score = src.score(),
    medals = src.medals.map { mapToMedalDto(it) },
    userDetails = userDetails,
)

fun mapToLeaderboardEntryPageDto(
    pageEntry: Page<LeaderboardEntry>,
    userDetailsById: Map<Long, UserDetailsDto>,
) = LeaderboardEntryPageDto(
    entries =
        pageEntry.content.map { entry ->
            mapToLeaderboardEntryDto(
                entry,
                userDetailsById[entry.userId]
                    ?: throw NoSuchElementException("No user details found for user id ${entry.userId}"),
            )
        },
    totalEntriesCount = pageEntry.totalElements,
)
