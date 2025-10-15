package com.stockcomp.leaderboard.leaderboard

import com.stockcomp.common.BaseEntity
import com.stockcomp.leaderboard.medal.Medal
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
    private val medals: MutableList<Medal> = mutableListOf()

    var contestCount: Int = 0
        private set

    var ranking: Int = 0
        private set

    var score: Int = 0
        private set

    fun addMedal(medal: Medal) {
        medals.add(medal)
    }
}