package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.ScheduledJobRunOutcome
import com.stockcomp.configuration.InvestmentOrderMaintenanceProperties
import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.contest.internal.ContestStatus
import com.stockcomp.participant.internal.Participant
import com.stockcomp.participant.internal.ParticipantService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class InvestmentOrderMaintenanceServiceTest {
    private val investmentOrderProcessingService = mockk<InvestmentOrderProcessingService>()
    private val participantService = mockk<ParticipantService>()
    private val contestService = mockk<ContestServiceExternal>()
    private val service =
        InvestmentOrderMaintenanceService(
            investmentOrderProcessingService = investmentOrderProcessingService,
            participantService = participantService,
            contestService = contestService,
            investmentOrderMaintenanceProperties = InvestmentOrderMaintenanceProperties(),
        )

    @Test
    fun `should process investment orders for active contest participants`() {
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
            )
        every { investmentOrderProcessingService.processInvestmentOrders(any()) } just Runs

        val result = service.maintainInvestmentOrders()

        assertEquals(ScheduledJobRunOutcome.SUCCESS, result.outcome)
        assertEquals(2, result.processedItems)
        assertEquals(0, result.failedItems)
        verify {
            investmentOrderProcessingService.processInvestmentOrders(FIRST_PARTICIPANT_ID)
            investmentOrderProcessingService.processInvestmentOrders(SECOND_PARTICIPANT_ID)
        }
    }

    @Test
    fun `should return skipped when no participants are processed`() {
        every { contestService.getActiveContests() } returns emptyList()

        val result = service.maintainInvestmentOrders()

        assertEquals(ScheduledJobRunOutcome.SKIPPED, result.outcome)
        assertEquals(0, result.processedItems)
        assertEquals(0, result.failedItems)
        assertEquals(1, result.skippedItems)
    }

    @Test
    fun `should continue processing participants after order processing fails`() {
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
            )
        every {
            investmentOrderProcessingService.processInvestmentOrders(FIRST_PARTICIPANT_ID)
        } throws IllegalStateException("FastFinance failed")
        every { investmentOrderProcessingService.processInvestmentOrders(SECOND_PARTICIPANT_ID) } just Runs

        val result = service.maintainInvestmentOrders()

        assertEquals(ScheduledJobRunOutcome.PARTIAL_FAILURE, result.outcome)
        assertEquals(1, result.processedItems)
        assertEquals(1, result.failedItems)
        verify {
            investmentOrderProcessingService.processInvestmentOrders(FIRST_PARTICIPANT_ID)
            investmentOrderProcessingService.processInvestmentOrders(SECOND_PARTICIPANT_ID)
        }
    }

    @Test
    fun `should return failure when all participant order processing fails`() {
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
            )
        every {
            investmentOrderProcessingService.processInvestmentOrders(any())
        } throws IllegalStateException("FastFinance failed")

        val result = service.maintainInvestmentOrders()

        assertEquals(ScheduledJobRunOutcome.FAILURE, result.outcome)
        assertEquals(0, result.processedItems)
        assertEquals(2, result.failedItems)
        verify {
            investmentOrderProcessingService.processInvestmentOrders(FIRST_PARTICIPANT_ID)
            investmentOrderProcessingService.processInvestmentOrders(SECOND_PARTICIPANT_ID)
        }
    }

    @Test
    fun `should stop processing investment orders when participant batch limit is reached`() {
        val limitedService =
            InvestmentOrderMaintenanceService(
                investmentOrderProcessingService = investmentOrderProcessingService,
                participantService = participantService,
                contestService = contestService,
                investmentOrderMaintenanceProperties = InvestmentOrderMaintenanceProperties(maxParticipantsPerRun = 2),
            )
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
                participant(THIRD_PARTICIPANT_ID),
            )
        every {
            investmentOrderProcessingService.processInvestmentOrders(FIRST_PARTICIPANT_ID)
        } throws IllegalStateException("FastFinance failed")
        every { investmentOrderProcessingService.processInvestmentOrders(SECOND_PARTICIPANT_ID) } just Runs

        val result = limitedService.maintainInvestmentOrders()

        assertEquals(ScheduledJobRunOutcome.PARTIAL_FAILURE, result.outcome)
        assertEquals(1, result.processedItems)
        assertEquals(1, result.failedItems)
        assertEquals(1, result.skippedItems)
        verify(exactly = 0) {
            investmentOrderProcessingService.processInvestmentOrders(THIRD_PARTICIPANT_ID)
        }
    }

    private fun participant(participantId: Long) =
        Participant(
            participantId = participantId,
            userId = participantId,
            contestId = CONTEST_ID,
        )

    private fun contest(contestId: Long) =
        ContestDto(
            contestId = contestId,
            startTime = LocalDateTime.now().minusDays(1),
            endTime = LocalDateTime.now().plusDays(1),
            contestName = "Contest $contestId",
            contestStatus = ContestStatus.RUNNING,
        )

    private companion object {
        const val CONTEST_ID = 10L
        const val FIRST_PARTICIPANT_ID = 101L
        const val SECOND_PARTICIPANT_ID = 102L
        const val THIRD_PARTICIPANT_ID = 103L
    }
}
