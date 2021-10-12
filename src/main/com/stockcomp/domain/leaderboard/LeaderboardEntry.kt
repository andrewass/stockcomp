package com.stockcomp.domain.leaderboard

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.user.User
import javax.persistence.*

@Entity
@Table(name = "T_LEADERBOARD_ENTRY")
class LeaderboardEntry(

    @Id
    @Column(name = "LEADERBOARD_ENTRY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var contestCount: Int = 0,

    var ranking: Int = 0,

    @OneToMany(mappedBy = "leaderboardEntry", cascade = [CascadeType.REMOVE])
    val medals: MutableList<Medal> = mutableListOf(),

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    ) : BaseEntity()