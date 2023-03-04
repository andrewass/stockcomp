package com.stockcomp.leaderboard.repository

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeaderboardEntryRepository : JpaRepository<LeaderboardEntry, Long> {

    fun findAllByOrderByRanking(): List<LeaderboardEntry>

    fun findAllByOrderByScore(): List<LeaderboardEntry>

    fun findByUser(user: User): LeaderboardEntry?
}