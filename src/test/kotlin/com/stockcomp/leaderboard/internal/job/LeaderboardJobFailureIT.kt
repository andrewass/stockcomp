package com.stockcomp.leaderboard.internal.job

import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.leaderboard.internal.LeaderboardService
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ControllerIntegrationTest
class LeaderboardJobFailureIT
    @Autowired
    constructor(
        private val leaderboardJobRepository: LeaderboardJobRepository,
        private val leaderboardJobScheduler: LeaderboardJobScheduler,
    ) {
        @MockkBean
        private lateinit var leaderboardService: LeaderboardService

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        fun `should persist retry state when leaderboard completion fails`() {
            val job = leaderboardJobRepository.saveAndFlush(LeaderboardJob(contestId = CONTEST_ID))
            every { leaderboardService.updateLeaderboard(CONTEST_ID) } throws IllegalStateException("Test failure")

            leaderboardJobScheduler.processLeaderboardJob()

            val updatedJob = leaderboardJobRepository.findById(requireNotNull(job.leaderboardJobId)).orElseThrow()
            assertTrue(
                leaderboardJobRepository.existsByContestIdAndJobStatusIn(
                    CONTEST_ID,
                    listOf(JobStatus.FAILED),
                ),
            )
            assertEquals(1, updatedJob.attempts())
            assertTrue(updatedJob.nextRunAt().isAfter(LocalDateTime.now()))
        }

        private companion object {
            const val CONTEST_ID = 9_001L
        }
    }
