package com.stockcomp.leaderboard.service

import com.stockcomp.contest.domain.Contest

interface LeaderboardOperationService {
    fun updateLeaderboardEntries(contest: Contest)
}