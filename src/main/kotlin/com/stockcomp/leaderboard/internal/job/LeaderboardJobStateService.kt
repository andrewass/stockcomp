package com.stockcomp.leaderboard.internal.job

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LeaderboardJobStateService(
    private val leaderboardJobRepository: LeaderboardJobRepository,
) {
    @Transactional
    fun claimNextDueJob(
        jobStatuses: List<JobStatus>,
        timeLimit: LocalDateTime,
    ): LeaderboardJobProcessingRequest? =
        leaderboardJobRepository
            .findFirstByJobStatusInAndNextRunAtLessThanEqualOrderByNextRunAtAsc(jobStatuses, timeLimit)
            ?.let { job ->
                LeaderboardJobProcessingRequest(
                    leaderboardJobId = requireNotNull(job.leaderboardJobId) { "Leaderboard job ID must not be null" },
                    contestId = job.contestId,
                    attempts = job.attempts(),
                )
            }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markAsCompleted(leaderboardJobId: Long) {
        val job = getJob(leaderboardJobId)
        job.markAsCompleted()
        leaderboardJobRepository.save(job)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markAsFailed(leaderboardJobId: Long) {
        val job = getJob(leaderboardJobId)
        job.markAsFailed()
        leaderboardJobRepository.save(job)
    }

    private fun getJob(leaderboardJobId: Long): LeaderboardJob =
        leaderboardJobRepository
            .findById(leaderboardJobId)
            .orElseThrow { NoSuchElementException("Leaderboard job $leaderboardJobId does not exist") }
}

data class LeaderboardJobProcessingRequest(
    val leaderboardJobId: Long,
    val contestId: Long,
    val attempts: Int,
)
