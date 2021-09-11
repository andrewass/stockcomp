package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.service.order.OrderProcessingService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DefaultContestServiceTest {

    @MockK
    private lateinit var contestRepository: ContestRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @MockK
    private lateinit var orderProcessingService: OrderProcessingService

    @InjectMockKs
    private lateinit var defaultContestService: DefaultContestService

    private val contestNumber = 33
    private val contest = Contest(contestNumber = contestNumber, startTime = LocalDateTime.now())

    private var contestSlot = slot<Contest>()

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)

        every {
            contestRepository.save(capture(contestSlot))
        } returns contest

        every {
            orderProcessingService.startOrderProcessing()
        } returns Unit

        every {
            orderProcessingService.stopOrderProcessing()
        } returns Unit
    }

    @Test
    fun `should start already created contest`() {
        every {
            contestRepository.findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)
        } returns createUpcomingContest()

        defaultContestService.startContest(contestNumber)

        assertTrue(contestSlot.captured.running)
        assertFalse(contestSlot.captured.completed)
    }

    @Test
    fun `should throw exception when trying to start a non-existing contest`() {
        every {
            contestRepository.findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should stop already running contest`() {
        every {
            contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        } returns createRunningContest()

        defaultContestService.stopContest(contestNumber)

        assertFalse(contestSlot.captured.completed)
        assertFalse(contestSlot.captured.running)
    }

    @Test
    fun `should throw exception when trying to stop a non-existing contest`() {
        every {
            contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should sign up for upcoming contest`() {

    }

    private fun createUpcomingContest() =
        Contest(startTime = LocalDateTime.now().plusWeeks(1), contestNumber = 100)

    private fun createRunningContest() =
        Contest(startTime = LocalDateTime.now().plusWeeks(1), contestNumber = 100, running = true)


}