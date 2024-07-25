package com.stockcomp.contest.domain

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.leaderboard.entity.Medal
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.EnumSet

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contestId: Long? = null,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    val contestName: String,

    @Enumerated(EnumType.STRING)
    var contestStatus: ContestStatus = ContestStatus.AWAITING_START,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val medals: MutableList<Medal> = mutableListOf()

) : BaseEntity() {

    fun isActive() = EnumSet.of(ContestStatus.AWAITING_START, ContestStatus.RUNNING, ContestStatus.STOPPED)
        .contains(contestStatus)

    override fun equals(other: Any?): Boolean =
        other is Contest && other.contestName == contestName

    override fun hashCode(): Int {
        return contestName.toInt()
    }
}