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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

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

    var contestSlot = slot<Contest>()

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
        } returns Optional.of(contest)

        defaultContestService.startContest(contestNumber)
        assertTrue(contestSlot.captured.running)
    }

    @Test
    fun `should throw exception when trying to start a non-existing contest`() {
        every {
            contestRepository.findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(contestNumber)
        } returns Optional.empty()

        assertThrows<IllegalStateException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should stop already running contest`() {
        every {
            contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        } returns Optional.of(contest)

        defaultContestService.stopContest(contestNumber)
    }

    @Test
    fun `should throw exception when trying to stop a non-running contest`() {
        every {
            contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        } returns Optional.empty()

        assertThrows<IllegalStateException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should sign up for upcoming contest`(){

    }
}