package com.stockcomp.leaderboard.internal.job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
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
    @SchedulerLock(name = "lockForLeaderboardJobs")
    fun process() {
        leaderboardJobRepository.fetchNextJobForProcessing(timeLimit = LocalDateTime.now())?.also {
            leaderboardJobProcessService.processJob(it)
        }
    }
}