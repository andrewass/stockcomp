package com.stockcomp.tasks

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.participant.MaintainParticipantService
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.ProcessOrdersService
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultContestTasksTest {

    @MockK
    private lateinit var contestRepository: ContestRepository

    @RelaxedMockK
    private lateinit var maintainParticipantService: MaintainParticipantService

    @RelaxedMockK
    private lateinit var processOrdersService: ProcessOrdersService

    @RelaxedMockK
    private lateinit var investmentOrderService: InvestmentOrderService

    @RelaxedMockK
    private lateinit var leaderboardService: LeaderboardService

    @InjectMockKs
    private lateinit var contestTasks: DefaultContestTasks

    private val runningContest = createContest()

    @BeforeEach
    private fun setup() {
        every {
            contestRepository.findAllByContestStatus(ContestStatus.RUNNING)
        } returns listOf(runningContest)
    }

    @Test
    fun `should start all contest tasks`() = runTest {
        contestTasks.startContestTasks()

        verify {
            maintainParticipantService.maintainParticipants()
        }
        coVerify {
            processOrdersService.processInvestmentOrders()
        }
    }

    @Test
    fun `should stop all contest tasks`() = runTest {
        contestTasks.startContestTasks()
        contestTasks.stopContestTasks()
    }

    @Test
    fun `should not start tasks when no running contest exists`() = runTest {
        every {
            contestRepository.findAllByContestStatus(ContestStatus.RUNNING)
        } returns emptyList()

        contestTasks.startContestTasks()

        verify(exactly = 0) {
            maintainParticipantService.maintainParticipants()
        }
        coVerify(exactly = 0) {
            processOrdersService.processInvestmentOrders()
        }
    }

    @Test
    fun `should throw exception when trying to start already running tasks`() = runTest {
        contestTasks.startContestTasks()

        assertThrows<IllegalStateException> {
            contestTasks.startContestTasks()
        }
    }

    private fun createContest() = Contest(
        startTime = LocalDateTime.now(),
        contestNumber = 1,
        endTime = LocalDateTime.now().plusWeeks(3),
        contestStatus = ContestStatus.RUNNING
    )
}
