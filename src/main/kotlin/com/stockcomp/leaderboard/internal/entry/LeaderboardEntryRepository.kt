package com.stockcomp.leaderboard.internal.entry

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LeaderboardEntryRepository : JpaRepository<LeaderboardEntry, Long> {
    @Query("SELECT entry FROM LeaderboardEntry entry ORDER BY entry.score DESC, entry.userId ASC")
    fun findAllByOrderByScoreDescAndUserIdAsc(): List<LeaderboardEntry>

    fun findByUserId(userId: Long): LeaderboardEntry?
}
