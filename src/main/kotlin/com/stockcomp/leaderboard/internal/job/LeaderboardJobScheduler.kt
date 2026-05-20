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
    @Scheduled(fixedDelayString = "\${scheduling.tasks.leaderboard.process-jobs.fixed-delay-ms}")
    @SchedulerLock(name = "lockForProcessLeaderboardJob")
    fun processLeaderboardJob() {
        leaderboardJobRepository
            .findFirstByJobStatusInAndNextRunAtLessThanEqualOrderByNextRunAtAsc(
                jobStatuses = openStatuses,
                timeLimit = LocalDateTime.now(),
            )?.also {
                leaderboardJobProcessService.processJob(it)
            }
    }

    @Transactional
    @Scheduled(fixedDelayString = "\${scheduling.tasks.leaderboard.create-jobs.fixed-delay-ms}")
    @SchedulerLock(name = "lockForCreateLeaderboardJob")
    fun createLeaderboardJobs() {
        contestService.getContestsAwaitingCompletion().forEach { contest ->
            if (!leaderboardJobRepository.existsByContestIdAndJobStatusIn(contest.contestId, openStatuses)) {
                leaderboardJobRepository.save(LeaderboardJob(contestId = contest.contestId))
            }
        }
    }
}
