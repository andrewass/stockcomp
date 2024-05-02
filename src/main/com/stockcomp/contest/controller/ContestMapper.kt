package com.stockcomp.contest.controller

import com.stockcomp.contest.domain.Contest
import org.springframework.data.domain.Page


fun mapToContestDto(source: Contest) = ContestDto(
    contestNumber = source.contestNumber,
    contestStatus = source.contestStatus,
    endTime = source.endTime,
    leaderboardUpdateStatus = source.leaderboardUpdateStatus,
    startTime = source.startTime
)

fun mapToContestPageDto(source: Page<Contest>) = ContestPageDto(
    contests = source.get().map { mapToContestDto(it) }.toList(),
    totalEntriesCount = source.totalElements
)