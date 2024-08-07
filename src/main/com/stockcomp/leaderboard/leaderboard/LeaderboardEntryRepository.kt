package com.stockcomp.leaderboard.leaderboard

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeaderboardEntryRepository : JpaRepository<LeaderboardEntry, Long> {
    fun findAllByOrderByScore(): List<LeaderboardEntry>

    fun findByUserId(userId: Long): LeaderboardEntry
}