package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest

interface LeaderboardOperationService {
    fun updateLeaderboardEntries(contest: Contest)
}