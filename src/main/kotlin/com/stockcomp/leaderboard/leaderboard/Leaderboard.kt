package com.stockcomp.leaderboard.leaderboard

import com.stockcomp.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "T_LEADERBOARD")
class Leaderboard(

    @Id
    @Column(name = "LEADERBOARD_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val leaderboardId: Long? = null,

    var contestCount: Int = 0,

    @OneToMany(mappedBy = "leaderboard", cascade = [CascadeType.ALL])
    val entries: MutableList<LeaderboardEntry> = mutableListOf()

) : BaseEntity()