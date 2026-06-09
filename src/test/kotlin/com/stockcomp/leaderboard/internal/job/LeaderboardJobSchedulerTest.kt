package com.stockcomp.leaderboard.internal.job

import com.stockcomp.common.ScheduledJobInstrumentation
import com.stockcomp.configuration.LeaderboardJobCreationProperties
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.contest.internal.ContestStatus
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class LeaderboardJobSchedulerTest {
    private val leaderboardJobRepository = mockk<LeaderboardJobRepository>()
    private val leaderboardJobProcessService = mockk<LeaderboardJobProcessService>()
    private val contestService = mockk<ContestServiceExternal>()
    private val meterRegistry = SimpleMeterRegistry()
    private val scheduler =
        LeaderboardJobScheduler(
            leaderboardJobRepository = leaderboardJobRepository,
            leaderboardJobProcessService = leaderboardJobProcessService,
            contestService = contestService,
            scheduledJobInstrumentation = ScheduledJobInstrumentation(meterRegistry),
            leaderboardJobCreationProperties = LeaderboardJobCreationProperties(maxContestsPerRun = 1),
        )

    @Test
    fun `should stop creating leaderboard jobs when contest batch limit is reached`() {
        every { contestService.getContestsAwaitingCompletion() } returns
            listOf(
                contest(FIRST_CONTEST_ID),
                contest(SECOND_CONTEST_ID),
                contest(THIRD_CONTEST_ID),
            )
        every { leaderboardJobRepository.existsByContestIdAndJobStatusIn(FIRST_CONTEST_ID, any()) } returns false
        every { leaderboardJobRepository.existsByContestIdAndJobStatusIn(SECOND_CONTEST_ID, any()) } returns true
        every { leaderboardJobRepository.existsByContestIdAndJobStatusIn(THIRD_CONTEST_ID, any()) } returns false
        every { leaderboardJobRepository.save(any<LeaderboardJob>()) } answers { firstArg() }

        scheduler.createLeaderboardJobs()

        verify(exactly = 1) {
            leaderboardJobRepository.save(
                match { it.contestId == FIRST_CONTEST_ID },
            )
        }
        verify(exactly = 0) {
            leaderboardJobRepository.save(
                match { it.contestId == THIRD_CONTEST_ID },
            )
        }
        assertEquals(1.0, itemCounter("processed"))
        assertEquals(2.0, itemCounter("skipped"))
    }

    private fun itemCounter(item: String): Double =
        meterRegistry
            .counter(
                ScheduledJobInstrumentation.ITEMS_METRIC,
                "job",
                "leaderboard-create-jobs",
                "item",
                item,
            ).count()

    private fun contest(contestId: Long) =
        ContestDto(
            contestId = contestId,
            startTime = LocalDateTime.now().minusDays(2),
            endTime = LocalDateTime.now().minusDays(1),
            contestName = "Contest $contestId",
            contestStatus = ContestStatus.AWAITING_COMPLETION,
        )

    private companion object {
        const val FIRST_CONTEST_ID = 1L
        const val SECOND_CONTEST_ID = 2L
        const val THIRD_CONTEST_ID = 3L
    }
}
