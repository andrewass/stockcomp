package com.stockcomp.domain.contest

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.LeaderboardUpdateStatus
import com.stockcomp.leaderboard.domain.Medal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val startTime: LocalDateTime,

    val endTime: LocalDateTime,

    val contestNumber: Int,

    @Enumerated(EnumType.STRING)
    var contestStatus : ContestStatus = ContestStatus.AWAITING_START,

    @Enumerated(EnumType.STRING)
    @Column(name = "LEADERBOARD_UPDATE")
    var leaderboardUpdateStatus: LeaderboardUpdateStatus = LeaderboardUpdateStatus.AWAITING,

    var participantCount: Int = 0,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val participants: MutableList<Participant> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf()

) : BaseEntity()