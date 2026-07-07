package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.LeaderboardServiceExternal
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceExternalImpl(
    private val leaderboardService: LeaderboardService,
) : LeaderboardServiceExternal {
    override fun updateLeaderboard(contestId: Long) {
        leaderboardService.updateLeaderboard(contestId)
    }
}
