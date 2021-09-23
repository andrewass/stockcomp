package com.stockcomp.service.investment

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.user.User
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentOrderRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DefaultInvestmentServiceTest {

    @MockK
    private lateinit var contestRepository: ContestRepository

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @InjectMockKs
    private lateinit var investmentService: DefaultInvestmentService

    private val username = "testUser"
    private val contestNumber = 100
    private val acceptedPrice = 150.00
    private val totalAmount = 120
    private val expirationTime = LocalDate.now().plusDays(10L)
    private val symbol = "AAPL"
    private val contest = createContest()
    private val participant = createParticipant()

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
        every {
            contestRepository.findByContestNumberAndRunningIsTrue(contestNumber)
        } returns contest

        every {
            participantRepository.findParticipantFromUsernameAndContest(username, contest)
        } returns listOf(participant)

        every {
            participantRepository.save(participant)
        } returns participant
    }

    @BeforeEach
    private fun resetData() {
        participant.investmentOrders.clear()
        participant.remainingFund = 20000.00
    }

    @Test
    fun `should get investment for a given symbol`() {
        participant.investments.add(
            Investment(id = 100L, symbol = symbol, amount = totalAmount, participant = participant)
        )
        val investment = investmentService.getInvestmentForSymbol(username, contestNumber, symbol)

        Assertions.assertEquals(symbol, investment!!.symbol)
        Assertions.assertEquals(totalAmount, investment.amount)
    }

    @Test
    fun `should get remaining funds`() {
        participant.remainingFund = 1400.00
        val remainingFunds = investmentService.getRemainingFunds(username, contestNumber)

        Assertions.assertEquals(1400.00, remainingFunds)
    }

    private fun verifyCommonFields(investmentOrder: InvestmentOrder) {
        Assertions.assertTrue(participant.investmentOrders.size == 1)
        Assertions.assertEquals(symbol, investmentOrder.symbol)
        Assertions.assertEquals(acceptedPrice, investmentOrder.acceptedPrice)
        Assertions.assertEquals(expirationTime.atStartOfDay(), investmentOrder.expirationTime)
        Assertions.assertEquals(totalAmount, investmentOrder.totalAmount)
        Assertions.assertEquals(totalAmount, investmentOrder.remainingAmount)
        Assertions.assertSame(participant, investmentOrder.participant)
    }

    private fun createInvestmentTransactionRequest() =
        InvestmentOrderRequest(
            contestNumber = contestNumber,
            symbol = symbol,
            amount = totalAmount,
            currency = "USD",
            expirationTime = expirationTime,
            acceptedPrice = acceptedPrice
        )

    private fun createContest() =
        Contest(
            contestNumber = 100,
            running = false,
            completed = true,
            startTime = LocalDateTime.now().minusDays(10)
        )

    private fun createParticipant() =
        Participant(
            user = User(username = username, password = "testPassword", email = "testEmail"),
            contest = contest
        )
}