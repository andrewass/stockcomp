package com.stockcomp.contest.internal

import com.stockcomp.common.BaseEntity
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

    var contestName: String

) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    var contestStatus: ContestStatus = ContestStatus.AWAITING_START

    fun isCompleted(): Boolean =
        contestStatus === ContestStatus.COMPLETED

    fun shouldStartContest(): Boolean =
        contestStatus == ContestStatus.AWAITING_START && startTime.isBefore(LocalDateTime.now())

    fun shouldStopFinishedContest(): Boolean =
        setOf(ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START).contains(contestStatus)
                && endTime.isBefore(LocalDateTime.now())

    fun startContest() {
        contestStatus = ContestStatus.RUNNING
    }

    fun stopFinishedContest() {
        contestStatus = ContestStatus.AWAITING_COMPLETION
    }

    fun setContestAsCompleted() {
        contestStatus = ContestStatus.COMPLETED
    }

    override fun equals(other: Any?): Boolean =
        other is Contest && other.contestName == contestName

    override fun hashCode(): Int {
        return contestName.toInt()
    }
}
