package com.stockcomp.leaderboard.leaderboard

import com.stockcomp.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "T_LEADERBOARD_JOB")
class LeaderboardJob(
    @Id
    @Column(name = "LEADERBOARD_JOB_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val leaderboardJobId: Long? = null,

    @Column(nullable = false)
    val contestId: Long? = null,

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
}


enum class JobStatus {
    COMPLETED,
    CREATED,
    FAILED
}