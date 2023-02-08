package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry

interface LeaderboardService {

    fun getSortedLeaderboardEntries(): List<LeaderboardEntry>

    fun getLeaderboardEntryForUser(username: String): LeaderboardEntry?
}