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

    var contestCount: Int = 0,

    var ranking: Int = 0,

    var score: Int = 0,

    @ManyToOne
    @JoinColumn(name = "LEADERBOARD_ID", nullable = false)
    val leaderboard: Leaderboard,

    @OneToMany(mappedBy = "leaderboardEntry", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf(),

    @Column(name = "USER_ID", nullable = false)
    val userId: Long,

    ) : BaseEntity()