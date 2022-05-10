package com.stockcomp.contest.service

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.ContestParticipationDto
import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant


fun mapToContestParticipationDto(contest : Contest, participant: Participant?) = ContestParticipationDto(
    contest = mapToContestDto(contest)
)

fun mapToContestDto(contest  : Contest) = ContestDto(
    contestNumber = contest.contestNumber,
    contestStatus = contest.contestStatus,
    endTime = contest.endTime,
    leaderboardUpdateStatus = contest.leaderboardUpdateStatus,
    participantCount = contest.participantCount,
    startTime = contest.startTime
)
