package com.stockcomp.contest.service

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.entity.Contest


fun mapToContestDto(contest  : Contest) = ContestDto(
    contestNumber = contest.contestNumber,
    contestStatus = contest.contestStatus,
    endTime = contest.endTime,
    leaderboardUpdateStatus = contest.leaderboardUpdateStatus,
    participantCount = contest.participantCount,
    startTime = contest.startTime
)
