package com.stockcomp.domain.leaderboard

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.contest.Contest
import javax.persistence.*

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