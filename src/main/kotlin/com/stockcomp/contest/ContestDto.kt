package com.stockcomp.contest

import com.stockcomp.contest.internal.Contest
import com.stockcomp.contest.internal.ContestStatus
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class ContestDto(
    val contestId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val contestName: String,
    val contestStatus: ContestStatus,
)

data class ContestPageDto(
    val contests: List<ContestDto>,
    val totalEntriesCount: Long,
)

fun toContestDto(source: Contest) =
    ContestDto(
        contestId = source.contestId!!,
        contestName = source.contestName,
        contestStatus = source.contestStatus,
        endTime = source.endTime,
        startTime = source.startTime,
    )

fun mapToContestPageDto(source: Page<Contest>) =
    ContestPageDto(
        contests = source.get().map { toContestDto(it) }.toList(),
        totalEntriesCount = source.totalElements,
    )
