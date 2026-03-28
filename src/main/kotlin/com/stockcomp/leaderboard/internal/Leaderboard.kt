package com.stockcomp.leaderboard.internal

import com.stockcomp.common.BaseEntity
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "T_LEADERBOARD")
class Leaderboard(
    @Id
    @Column(name = "LEADERBOARD_ID", nullable = false)
    val leaderboardId: Long? = null,
) : BaseEntity() {
    @OneToMany(mappedBy = "leaderboard", cascade = [CascadeType.ALL])
    private val entries: MutableList<LeaderboardEntry> = mutableListOf()

    @Column(name = "CONTEST_COUNT", nullable = false)
    private var contestCount: Int = 0

    fun addEntry(leaderboardEntry: LeaderboardEntry) {
        entries.add(leaderboardEntry)
    }

    fun contestCount(): Int = contestCount
}
