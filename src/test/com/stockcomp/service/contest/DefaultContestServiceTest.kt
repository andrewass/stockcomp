package com.stockcomp.service.contest

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.DefaultContestService
import com.stockcomp.user.entity.User
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.DefaultUserService
import com.stockcomp.contest.tasks.DefaultContestTasks
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
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
    private lateinit var contestTasks: DefaultContestTasks

    @InjectMockKs
    private lateinit var contestService: DefaultContestService

    private val contestNumber = 33
    private val username = "testUser"
    private val contest = Contest(
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusMonths(2L), contestNumber = contestNumber
    )
    private val user = User(username = username, email = "testEmail", password = "testPassword", country = "Canada")
    private val participant = Participant(user = user, contest = contest, rank = 1)

    private var contestSlot = slot<Contest>()
    private var participantSlot = slot<Participant>()

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)

        every {
            contestRepository.save(capture(contestSlot))
        } returns contest

        every {
            contestTasks.startOrderProcessing()
        } returns Unit

        every {
            contestTasks.stopOrderProcessing()
        } returns Unit

        every {
            contestTasks.startMaintainInvestments()
        } returns Unit

        every {
            contestTasks.stopMaintainInvestments()
        } returns Unit
    }

    @Test
    fun `should start already created contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)
        } returns createFutureContest()

        contestService.startContest(contestNumber)

        assertEquals(ContestStatus.RUNNING, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to start a non-existing contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)
        } returns null

        assertThrows<NoSuchElementException> {
            contestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should stop already running contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
        } returns createRunningContest()

        contestService.stopContest(contestNumber)

        assertEquals(ContestStatus.STOPPED, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to stop a non-existing contest`() {
        every {
            contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
        } returns null

        assertThrows<NoSuchElementException> {
            contestService.startContest(contestNumber)
        }
    }

    @Test
    fun `should complete already running contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns createRunningContest()

        contestService.completeContest(contestNumber)

        assertEquals(ContestStatus.COMPLETED, contestSlot.captured.contestStatus)
    }

    @Test
    fun `should throw exception when trying to complete a non-existing contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            contestService.completeContest(contestNumber)
        }
    }

    @Disabled
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

        contestService.signUpUser(username, contestNumber)

        assertEquals(participantSlot.captured.contest, runningContest)
        assertEquals(participantSlot.captured.user, user)
    }

    @Test
    fun `should throw exception when trying to sign up for a non-existing contest`() {
        every {
            contestRepository.findByContestNumber(contestNumber)
        } returns null

        assertThrows<NoSuchElementException> {
            contestService.signUpUser(username, contestNumber)
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