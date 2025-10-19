package com.stockcomp.leaderboard.internal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
) {

    fun updateLeaderboard(contestId: Long) {
    }

    fun getLeaderboard(): Leaderboard {
        val leaderboards = leaderboardRepository.findAll()
        if (leaderboards.size != 1) {
            throw IllegalStateException("Should only exist 1 leaderboard")
        }
        return leaderboards.first()
    }
}