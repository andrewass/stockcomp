package com.stockcomp.contest.internal

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestPageDto
import org.springframework.data.domain.Page

fun toContestDto(source: Contest) =
    ContestDto(
        contestId = requireNotNull(source.contestId) { "Contest id is null while mapping ContestDto" },
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
