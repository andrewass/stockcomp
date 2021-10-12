package com.stockcomp.service.leaderboard

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.leaderboard.LeaderboardEntry

interface LeaderboardService {

    fun updateLeaderboard(contest : Contest)

    fun getSortedLeaderboard() : List<LeaderboardEntry>
}