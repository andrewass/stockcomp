package com.stockcomp.leaderboard.internal.job

import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.leaderboard.internal.LeaderboardService
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ControllerIntegrationTest
class LeaderboardJobFailureIT
    @Autowired
    constructor(
        private val jdbcTemplate: JdbcTemplate,
        private val leaderboardJobRepository: LeaderboardJobRepository,
        private val leaderboardJobScheduler: LeaderboardJobScheduler,
    ) {
        @MockkBean
        private lateinit var leaderboardService: LeaderboardService

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        fun `should persist retry state when leaderboard completion fails`() {
            val now = LocalDateTime.now()
            val contestId =
                requireNotNull(
                    jdbcTemplate.queryForObject(
                        """
                        insert into t_contest (contest_name, start_time, end_time, contest_status, date_created, date_updated)
                        values ('Leaderboard Job Failure Contest', ?, ?, 'COMPLETED', current_timestamp, current_timestamp)
                        returning contest_id
                        """.trimIndent(),
                        Long::class.java,
                        now,
                        now.plusDays(1),
                    ),
                )
            try {
                val job = leaderboardJobRepository.saveAndFlush(LeaderboardJob(contestId = contestId))
                every { leaderboardService.updateLeaderboard(contestId) } throws IllegalStateException("Test failure")

                leaderboardJobScheduler.processLeaderboardJob()

                val updatedJob = leaderboardJobRepository.findById(requireNotNull(job.leaderboardJobId)).orElseThrow()
                assertTrue(
                    leaderboardJobRepository.existsByContestIdAndJobStatusIn(
                        contestId,
                        listOf(JobStatus.FAILED),
                    ),
                )
                assertEquals(1, updatedJob.attempts())
                assertTrue(updatedJob.nextRunAt().isAfter(LocalDateTime.now()))
            } finally {
                jdbcTemplate.update("delete from t_contest where contest_id = ?", contestId)
            }
        }
    }
