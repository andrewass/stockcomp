package com.stockcomp.leaderboard.internal

import com.stockcomp.common.BaseEntity
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import jakarta.persistence.*

@Entity
@Table(name = "T_LEADERBOARD")
class Leaderboard(

    @Id
    @Column(name = "LEADERBOARD_ID", nullable = false)
    val leaderboardId: Long? = null,

    ) : BaseEntity() {

    @OneToMany(mappedBy = "leaderboard", cascade = [CascadeType.ALL])
    private val entries: MutableList<LeaderboardEntry> = mutableListOf()

    var contestCount: Int = 0
        private set

    fun addEntry(leaderboardEntry: LeaderboardEntry) {
        entries.add(leaderboardEntry)
    }

    fun updateEntry(userId: Long) {
        entries.find { it.userId == userId }
    }

    fun recalculateRankings() {

    }

    private fun incrementContestCount() {
        contestCount += 1
    }

}