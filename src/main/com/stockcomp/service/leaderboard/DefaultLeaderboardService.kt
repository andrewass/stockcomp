package com.stockcomp.service.leaderboard

import com.stockcomp.domain.leaderboard.LeaderboardEntry
import com.stockcomp.repository.LeaderboardEntryRepository

class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository
) : LeaderboardService {

    override fun updateLeaderboard() {
        TODO("Not yet implemented")
    }

    override fun getSortedLeaderboard(): List<LeaderboardEntry> {
        return leaderboardEntryRepository.findAllByOrderByRankingAsc()
    }

    override fun getLeaderboardEntryForUser(username: String): LeaderboardEntry? {
        TODO("Not yet implemented")
    }
}