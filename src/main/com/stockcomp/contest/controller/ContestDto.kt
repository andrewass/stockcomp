package com.stockcomp.contest.controller

import com.stockcomp.contest.domain.Contest
import com.stockcomp.contest.domain.ContestStatus
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class ContestDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestName: String,
    val contestStatus: ContestStatus,
)

data class ContestPageDto(
    val contests: List<ContestDto>,
    val totalEntriesCount: Long
)

fun mapToContestDto(source: Contest) = ContestDto(
    contestName = source.contestName,
    contestStatus = source.contestStatus,
    endTime = source.endTime,
    startTime = source.startTime
)

fun mapToContestPageDto(source: Page<Contest>) = ContestPageDto(
    contests = source.get().map { mapToContestDto(it) }.toList(),
    totalEntriesCount = source.totalElements
)