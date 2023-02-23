package com.stockcomp.contest.entity

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.leaderboard.entity.Medal
import com.stockcomp.participant.entity.Participant
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    val contestNumber: Int,

    @Enumerated(EnumType.STRING)
    var contestStatus: ContestStatus = ContestStatus.AWAITING_START,

    @Enumerated(EnumType.STRING)
    @Column(name = "LEADERBOARD_UPDATE")
    var leaderboardUpdateStatus: LeaderboardUpdateStatus = LeaderboardUpdateStatus.AWAITING,

    var participantCount: Int = 0,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val participants: MutableList<Participant> = mutableListOf(),

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf()

) : BaseEntity() {

    override fun equals(other: Any?): Boolean =
        other is Contest && other.contestNumber == contestNumber
}