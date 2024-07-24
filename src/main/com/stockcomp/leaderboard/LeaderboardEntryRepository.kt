package com.stockcomp.leaderboard

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeaderboardEntryRepository : JpaRepository<LeaderboardEntry, Long> {
    fun findAllByOrderByScore(): List<LeaderboardEntry>

    fun findByUser(user: User): LeaderboardEntry?
}