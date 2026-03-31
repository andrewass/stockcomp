package com.stockcomp.contest.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(name = "T_CONTEST")
class Contest(
    @Id
    @Column(name = "CONTEST_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contestId: Long? = null,
    @Column(name = "START_TIME", nullable = false)
    private var _startTime: LocalDateTime,
    @Column(name = "END_TIME", nullable = false)
    private var _endTime: LocalDateTime,
    @Column(name = "CONTEST_NAME", nullable = false)
    private var _contestName: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "CONTEST_STATUS", nullable = false)
    private var _contestStatus: ContestStatus = ContestStatus.AWAITING_START,
) : BaseEntity() {
    constructor(
        contestName: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        contestStatus: ContestStatus = ContestStatus.AWAITING_START,
        contestId: Long? = null,
    ) : this(
        contestId = contestId,
        _startTime = startTime,
        _endTime = endTime,
        _contestName = contestName,
        _contestStatus = contestStatus,
    )

    val contestStatus: ContestStatus
        get() = _contestStatus

    val contestName: String
        get() = _contestName

    val startTime: LocalDateTime
        get() = _startTime

    val endTime: LocalDateTime
        get() = _endTime

    fun isCompleted(): Boolean = _contestStatus === ContestStatus.COMPLETED

    fun shouldStartContest(now: LocalDateTime): Boolean = _contestStatus == ContestStatus.AWAITING_START && _startTime.isBefore(now)

    fun shouldStopFinishedContest(now: LocalDateTime): Boolean =
        setOf(ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START).contains(_contestStatus) &&
            _endTime.isBefore(now)

    fun startContest() {
        _contestStatus = ContestStatus.RUNNING
    }

    fun stopFinishedContest() {
        _contestStatus = ContestStatus.AWAITING_COMPLETION
    }

    fun setContestAsCompleted() {
        _contestStatus = ContestStatus.COMPLETED
    }

    fun updateContestStatus(newStatus: ContestStatus) {
        _contestStatus = newStatus
    }

    fun renameContest(newContestName: String) {
        _contestName = newContestName
    }

    fun updateStartTimePreservingDuration(newStartTime: LocalDateTime) {
        val duration = Duration.between(_startTime, _endTime)
        _startTime = newStartTime
        _endTime = newStartTime.plus(duration)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Contest) return false
        return contestId != null && contestId == other.contestId
    }

    override fun hashCode(): Int = contestId?.hashCode() ?: System.identityHashCode(this)
}
