package com.stockcomp.service

import com.stockcomp.entity.contest.Contest
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.order.OrderProcessingService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ContestServiceTest {

    @MockK
    private lateinit var contestRepository: ContestRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @MockK
    private lateinit var orderProcessingService: OrderProcessingService

    @InjectMockKs
    private lateinit var contestService: ContestService

    private val contestNumber = 33
    private val contest = Contest(contestNumber = contestNumber, startTime = LocalDateTime.now())

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)

        every {
            contestRepository.save(any<Contest>())
        } returns contest

        every {
            orderProcessingService.startOrderProcessing()
        } returns Unit

        every {
            orderProcessingService.stopOrderProcessing()
        } returns Unit
    }

    @Test
    fun `should create contest`() {
        val request = CreateContestRequest(startTime = LocalDateTime.now(), contestNumber = contestNumber)

        val createdContest = contestService.createContest(request)

        assertEquals(contest, createdContest)
    }

    @Test
    fun `should start already created contest`() {
        every {
            contestRepository.findContestByContestNumberAndInPreStartModeIsTrue(contestNumber)
        } returns Optional.of(contest)

        contestService.startContest(contestNumber)
    }

    @Test
    fun `should throw exception when trying to start a non-existing contest`() {
        every {
            contestRepository.findContestByContestNumberAndInPreStartModeIsTrue(contestNumber)
        } returns Optional.empty()

        assertThrows<IllegalStateException> {
            contestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should stop already running contest`() {
        every {
            contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)
        } returns Optional.of(contest)

        contestService.stopContest(contestNumber)
    }

    @Test
    fun `should throw exception when trying to stop a non-running contest`() {
        every {
            contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)
        } returns Optional.empty()

        assertThrows<IllegalStateException> {
            contestService.startContest(contestNumber)
        }
    }
}