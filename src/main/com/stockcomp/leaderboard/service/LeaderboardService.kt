package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.user.domain.User
import org.springframework.data.domain.Page

interface LeaderboardService {

    fun getSortedLeaderboardEntries(pageNumber: Int, pageSize: Int): Page<LeaderboardEntry>

    fun getLeaderboardEntryForEmail(email: String): LeaderboardEntry?

    fun getLeaderboardEntryForUser(user: User): LeaderboardEntry?

    fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry>

    fun saveEntry(leaderboardEntry: LeaderboardEntry)

    fun saveAllEntries(leaderboardEntries : List<LeaderboardEntry>)
}