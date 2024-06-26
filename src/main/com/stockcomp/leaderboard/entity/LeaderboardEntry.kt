package com.stockcomp.leaderboard.entity

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.contest.domain.Contest
import com.stockcomp.user.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "T_LEADERBOARD_ENTRY")
class LeaderboardEntry(

    @Id
    @Column(name = "LEADERBOARD_ENTRY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var contestCount: Int = 0,

    var ranking: Int = 0,

    var score: Int = 0,

    @OneToMany(mappedBy = "leaderboardEntry", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf(),

    @OneToOne
    @JoinColumn(name = "LAST_CONTEST_ID")
    var lastContest: Contest? = null,

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    ) : BaseEntity()