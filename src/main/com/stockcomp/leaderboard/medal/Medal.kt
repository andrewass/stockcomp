package com.stockcomp.leaderboard.medal

import com.stockcomp.common.BaseEntity
import com.stockcomp.leaderboard.leaderboard.LeaderboardEntry
import jakarta.persistence.*

@Entity
@Table(name = "T_MEDAL")
class Medal(

    @Id
    @Column(name = "MEDAL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    val medalValue: MedalValue,

    val position: Int,

    @Column(name = "CONTEST_ID", nullable = false)
    val contestId: Long,

    @ManyToOne
    @JoinColumn(name = "LEADERBOARD_ENTRY_ID", nullable = false)
    val leaderboardEntry: LeaderboardEntry

) : BaseEntity()