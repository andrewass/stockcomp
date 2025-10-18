package com.stockcomp.leaderboard.leaderboard.job

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class LeaderboardJobRunner(
    private val leaderboardJobRepository: LeaderboardJobRepository,
    private val leaderboardJobProcessService: LeaderboardJobProcessService
) {

    @Transactional
    @Scheduled(fixedDelay = 5000)
    fun process() {
        leaderboardJobRepository.fetchNextJobForProcessing(timeLimit = LocalDateTime.now())?.also {
            leaderboardJobProcessService.processJob(it)
        }
    }
}