package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.leaderboard.entity.LeaderboardEntry

interface LeaderboardService {

    fun updateLeaderboard(contest: Contest)

    fun getSortedLeaderboardEntries(): List<LeaderboardEntry>

    fun getLeaderboardEntryForUser(username: String): LeaderboardEntry?
}