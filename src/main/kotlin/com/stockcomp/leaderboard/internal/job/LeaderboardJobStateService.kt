package com.stockcomp.leaderboard.internal.job

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class LeaderboardJobStateService(
    private val leaderboardJobRepository: LeaderboardJobRepository,
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markAsCompleted(job: LeaderboardJob) {
        job.markAsCompleted()
        leaderboardJobRepository.save(job)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markAsFailed(job: LeaderboardJob) {
        job.markAsFailed()
        leaderboardJobRepository.save(job)
    }
}