package com.stockcomp.leaderboard.leaderboard.job

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LeaderboardJobRepository : JpaRepository<LeaderboardJob, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT j FROM LeaderboardJob j
        WHERE j.jobStatus in ('CREATED', 'FAILED')
          AND j.nextRunAt <= :timeLimit
          ORDER BY j.nextRunAt
          LIMIT 1
    """
    )
    fun fetchNextJobForProcessing(@Param("timeLimit") timeLimit: LocalDateTime): LeaderboardJob?
}