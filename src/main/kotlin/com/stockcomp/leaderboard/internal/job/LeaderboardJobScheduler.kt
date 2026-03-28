package com.stockcomp.leaderboard.internal.job

import com.stockcomp.contest.ContestServiceExternal
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class LeaderboardJobScheduler(
    private val leaderboardJobRepository: LeaderboardJobRepository,
    private val leaderboardJobProcessService: LeaderboardJobProcessService,
    private val contestService: ContestServiceExternal,
) {
    private val openStatuses = listOf(JobStatus.CREATED, JobStatus.FAILED)

    @Transactional
    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(name = "lockForProcessLeaderboardJob")
    fun processLeaderboardJob() {
        leaderboardJobRepository.findFirstByJobStatusInAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
            jobStatuses = openStatuses,
            timeLimit = LocalDateTime.now(),
        )?.also {
            leaderboardJobProcessService.processJob(it)
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 15000)
    @SchedulerLock(name = "lockForCreateLeaderboardJob")
    fun createLeaderboardJobs() {
        contestService.getContestsAwaitingCompletion().forEach { contest ->
            if (!leaderboardJobRepository.existsByContestIdAndJobStatusIn(contest.contestId, openStatuses)) {
                leaderboardJobRepository.save(LeaderboardJob(contestId = contest.contestId))
            }
        }
    }
}
