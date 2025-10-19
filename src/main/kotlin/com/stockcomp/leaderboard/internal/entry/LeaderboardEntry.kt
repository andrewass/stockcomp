package com.stockcomp.leaderboard.internal.entry

import com.stockcomp.common.BaseEntity
import com.stockcomp.leaderboard.internal.medal.Medal
import com.stockcomp.leaderboard.internal.Leaderboard
import jakarta.persistence.*

@Entity
@Table(name = "T_LEADERBOARD_ENTRY")
class LeaderboardEntry(

    @Id
    @Column(name = "LEADERBOARD_ENTRY_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val leaderboardEntryId: Long? = null,

    @ManyToOne
    @JoinColumn(name = "LEADERBOARD_ID", nullable = false)
    val leaderboard: Leaderboard,

    @Column(name = "USER_ID", nullable = false)
    val userId: Long,

    ) : BaseEntity() {

    @OneToMany(mappedBy = "leaderboardEntry", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _medals: MutableList<Medal> = mutableListOf()

    val medals: List<Medal>
        get() = _medals.toList()

    var contestCount: Int = 0
        private set

    var ranking: Int = 0
        private set

    var score: Int = 0
        private set

    fun addMedal(medal: Medal) {
        _medals.add(medal)
    }

    fun incrementContestCount() {
        contestCount += 1
    }
}