package com.stockcomp.domain.user

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.leaderboard.LeaderboardEntry
import javax.persistence.*

@Entity
@Table(name = "T_USER")
class User(

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val username: String,

    val password: String,

    val email: String,

    @Enumerated(EnumType.STRING)
    val userRole: Role = Role.USER,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    val refreshTokens: MutableList<RefreshToken> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    val leaderboardEntries : MutableList<LeaderboardEntry> = mutableListOf()

    ) : BaseEntity()