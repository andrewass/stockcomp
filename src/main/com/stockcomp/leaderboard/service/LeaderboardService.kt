package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.user.entity.User

interface LeaderboardService {

    fun getSortedLeaderboardEntries(): List<LeaderboardEntry>

    fun getLeaderboardEntryForEmail(email: String): LeaderboardEntry?

    fun getLeaderboardEntryForUser(user: User): LeaderboardEntry?

    fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry>

    fun saveEntry(leaderboardEntry: LeaderboardEntry)

    fun saveAllEntries(leaderboardEntries : List<LeaderboardEntry>)
}