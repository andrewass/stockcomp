package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.user.User
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.order.OrderProcessingService
import com.stockcomp.service.user.DefaultUserService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
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
    private lateinit var userService: DefaultUserService

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @MockK
    private lateinit var orderProcessingService: OrderProcessingService

    @InjectMockKs
    private lateinit var defaultContestService: DefaultContestService

    private val contestNumber = 33
    private val username = "testUser"
    private val contest = Contest(
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusMonths(2L), contestNumber = contestNumber
    )
    private val user = User(username = username, email = "testEmail", password = "testPassword", country = "Canada")
    private val participant = Participant(user = user, contest = contest)

    private var contestSlot = slot<Contest>()
    private var participantSlot = slot<Participant>()

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
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)
        } returns createFutureContest()

        defaultContestService.startContest(contestNumber)

        assertEquals(ContestStatus.RUNNING, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to start a non-existing contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should stop already running contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
        } returns createRunningContest()

        defaultContestService.stopContest(contestNumber)

        assertEquals(ContestStatus.STOPPED, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to stop a non-existing contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should complete already running contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns createRunningContest()

        defaultContestService.completeContest(contestNumber)

        assertEquals(ContestStatus.COMPLETED, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to complete a non-existing contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.completeContest(contestNumber)
        }
    }

    @Test
    fun `should sign up user for contest`() {
        val runningContest = createRunningContest()

        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns runningContest

        every {
            userService.findUserByUsername(username)
        } returns user

        every {
            participantRepository.save(capture(participantSlot))
        } returns participant

        defaultContestService.signUpUser(username, contestNumber)

        assertEquals(participantSlot.captured.contest, runningContest)
        assertEquals(participantSlot.captured.user, user)
    }

    @Test
    fun `should throw exception when trying to sign up for a non-existing contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            defaultContestService.signUpUser(username, contestNumber)
        }
    }

    @Test
    fun `should get upcoming contests`() {
        val runningContest = createRunningContest()

        every {
            contestRepository.findAll()
        } returns listOf(runningContest)

        every {
            participantRepository.findParticipantFromUsernameAndContest(username, runningContest)
        } returns listOf(participant)

        val upcomingContests = defaultContestService.getUpcomingContestsParticipant(username)

        assertEquals(upcomingContests.size, 1)
        upcomingContests[0].let {
            assertEquals(contestNumber, it.contestNumber)
            assertEquals(ContestStatus.RUNNING.decode, it.contestStatus)
            assertEquals(true, it.userParticipating, )
            assertEquals(runningContest.startTime, it.startTime)
        }
    }

    private fun createFutureContest() =
        Contest(
            startTime = LocalDateTime.now().plusWeeks(1L), endTime = LocalDateTime.now().plusWeeks(5L),
            contestNumber = contestNumber
        )

    private fun createRunningContest() =
        Contest(
            startTime = LocalDateTime.now().minusWeeks(1), endTime = LocalDateTime.now().plusWeeks(7L),
            contestNumber = contestNumber, contestStatus = ContestStatus.RUNNING
        )
}