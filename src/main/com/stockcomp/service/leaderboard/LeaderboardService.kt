package com.stockcomp.service.leaderboard

import com.stockcomp.domain.contest.Contest
import com.stockcomp.response.leaderboard.LeaderboardEntryDto

interface LeaderboardService {

    fun updateLeaderboard(contest: Contest)

    fun getSortedLeaderboardEntries(): List<LeaderboardEntryDto>

    fun getLeaderboardEntryForUser(username: String): LeaderboardEntryDto
}