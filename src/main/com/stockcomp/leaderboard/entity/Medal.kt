package com.stockcomp.leaderboard.entity

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.contest.entity.Contest
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

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID", nullable = false)
    val contest: Contest,

    @ManyToOne
    @JoinColumn(name = "LEADERBOARD_ENTRY_ID", nullable = false)
    val leaderboardEntry: LeaderboardEntry

) : BaseEntity()