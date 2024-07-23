package com.stockcomp.contest.domain

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.leaderboard.entity.Medal
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contestId: Long? = null,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    val contestNumber: Int,

    @Enumerated(EnumType.STRING)
    var contestStatus: ContestStatus = ContestStatus.AWAITING_START,

    @Enumerated(EnumType.STRING)
    @Column(name = "LEADERBOARD_UPDATE")
    var leaderboardUpdateStatus: LeaderboardUpdateStatus = LeaderboardUpdateStatus.AWAITING,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf()

) : BaseEntity() {

    fun getParticipantCount(): Int = 0

    override fun equals(other: Any?): Boolean =
        other is Contest && other.contestNumber == contestNumber

    override fun hashCode(): Int {
        return contestNumber
    }
}