package com.stockcomp.leaderboard.service

import com.stockcomp.domain.contest.Contest
import com.stockcomp.dto.leaderboard.LeaderboardEntryDto

interface LeaderboardService {

    fun updateLeaderboard(contest: Contest)

    fun getSortedLeaderboardEntries(): List<LeaderboardEntryDto>

    fun getLeaderboardEntryForUser(username: String): LeaderboardEntryDto?
}