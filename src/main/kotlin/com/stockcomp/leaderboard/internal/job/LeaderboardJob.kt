package com.stockcomp.leaderboard.internal.job

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "T_LEADERBOARD_JOB")
class LeaderboardJob(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val leaderboardJobId: Long? = null,
    @Column(nullable = false)
    val contestId: Long,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var jobStatus = JobStatus.CREATED

    @Column(nullable = false)
    private var nextRunAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    private var attempts: Int = 0

    fun markAsCompleted() {
        jobStatus = JobStatus.COMPLETED
        attempts += 1
    }

    fun markAsFailed() {
        jobStatus = JobStatus.FAILED
        attempts += 1
        nextRunAt = LocalDateTime.now().plusMinutes(1L)
    }

    fun nextRunAt(): LocalDateTime = nextRunAt

    fun attempts(): Int = attempts
}

enum class JobStatus {
    COMPLETED,
    CREATED,
    FAILED,
}
