package com.stockcomp.leaderboard.leaderboard.job

import com.stockcomp.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "T_LEADERBOARD_JOB")
class LeaderboardJob(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val leaderboardJobId: Long? = null,

    @Column(nullable = false)
    val contestId: Long

) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var jobStatus = JobStatus.CREATED

    @Column(nullable = false)
    var nextRunAt: LocalDateTime = LocalDateTime.now()
        private set

    @Column(nullable = false)
    var attempts: Int = 0
        private set

    fun markAsCompleted() {
        jobStatus = JobStatus.COMPLETED
        attempts += 1
    }

    fun markAsFailed() {
        jobStatus = JobStatus.FAILED
        attempts += 1
        nextRunAt = LocalDateTime.now().plusMinutes(1L)
    }
}


enum class JobStatus {
    COMPLETED,
    CREATED,
    FAILED,
}