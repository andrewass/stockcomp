package com.stockcomp.participant.internal.investment

import com.stockcomp.common.ScheduledJobRunOutcome
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

class InvestmentMaintenanceServiceTest {
    private val participantService = mockk<ParticipantService>()
    private val contestService = mockk<ContestServiceExternal>()
    private val investmentProcessingService = mockk<InvestmentProcessingService>()
    private val service =
        InvestmentMaintenanceService(
            participantService = participantService,
            contestService = contestService,
            investmentProcessingService = investmentProcessingService,
        )

    @Test
    fun `should maintain investments for active contest participants`() {
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
            )
        every { investmentProcessingService.maintainInvestments(any()) } just Runs

        val result = service.maintainInvestments()

        assertEquals(ScheduledJobRunOutcome.SUCCESS, result.outcome)
        assertEquals(2, result.processedItems)
        verify {
            investmentProcessingService.maintainInvestments(FIRST_PARTICIPANT_ID)
            investmentProcessingService.maintainInvestments(SECOND_PARTICIPANT_ID)
        }
    }

    @Test
    fun `should return skipped when no participants are processed`() {
        every { contestService.getActiveContests() } returns emptyList()

        val result = service.maintainInvestments()

        assertEquals(ScheduledJobRunOutcome.SKIPPED, result.outcome)
        assertEquals(0, result.processedItems)
        assertEquals(1, result.skippedItems)
    }

    @Test
    fun `should return failure with processed count when maintenance fails`() {
        every { contestService.getActiveContests() } returns listOf(contest(CONTEST_ID))
        every { participantService.getAllByContest(CONTEST_ID) } returns
            listOf(
                participant(FIRST_PARTICIPANT_ID),
                participant(SECOND_PARTICIPANT_ID),
            )
        every { investmentProcessingService.maintainInvestments(FIRST_PARTICIPANT_ID) } just Runs
        every {
            investmentProcessingService.maintainInvestments(SECOND_PARTICIPANT_ID)
        } throws IllegalStateException("FastFinance failed")

        val result = service.maintainInvestments()

        assertEquals(ScheduledJobRunOutcome.FAILURE, result.outcome)
        assertEquals(1, result.processedItems)
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
    }
}
