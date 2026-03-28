package com.stockcomp.leaderboard.internal.job

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LeaderboardJobRepository : JpaRepository<LeaderboardJob, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findFirstByJobStatusInAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
        jobStatuses: List<JobStatus>,
        timeLimit: LocalDateTime,
    ): LeaderboardJob?

    fun existsByContestIdAndJobStatusIn(
        contestId: Long,
        jobStatuses: List<JobStatus>,
    ): Boolean

    fun countByContestId(contestId: Long): Long
}
