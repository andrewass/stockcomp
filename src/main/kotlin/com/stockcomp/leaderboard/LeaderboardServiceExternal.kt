package com.stockcomp.leaderboard

import com.stockcomp.leaderboard.internal.LeaderboardService
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceExternal(
    private val leaderboardService: LeaderboardService,
) {
    fun updateLeaderboardEntries(contestId: Long) {
        leaderboardService.updateLeaderboard(contestId)
    }
}
