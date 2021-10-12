package com.stockcomp.service.leaderboard

import com.stockcomp.domain.leaderboard.LeaderboardEntry

interface LeaderboardService {

    fun updateLeaderboard()

    fun getSortedLeaderboard() : List<LeaderboardEntry>

    fun getLeaderboardEntryForUser(username: String) : LeaderboardEntry?
}