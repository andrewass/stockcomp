package com.stockcomp.service

import com.stockcomp.domain.contest.*
import com.stockcomp.domain.user.User
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.service.investment.DefaultInvestmentService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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
            contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)
        } returns Optional.of(contest)
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
    fun `should place buy order for participant`() {
        val request = createInvestmentTransactionRequest()

        investmentService.placeBuyOrder(request, username)

        val awaitingOrder = participant.investmentOrders[0]

        verifyCommonFields(awaitingOrder)
        assertEquals(TransactionType.BUY, awaitingOrder.transactionType)
        verify { participantRepository.save(participant) }
    }

    @Test
    fun `should place sell order for participant`() {
        val request = createInvestmentTransactionRequest()

        investmentService.placeSellOrder(request, username)

        val awaitingOrder = participant.investmentOrders[0]

        verifyCommonFields(awaitingOrder)
        assertEquals(TransactionType.SELL, awaitingOrder.transactionType)
        verify { participantRepository.save(participant) }
    }

    @Test
    fun `should get investment for a given symbol`() {
        participant.portfolio.investments.add(
            Investment(
                id = 100L,
                symbol = symbol,
                amount = totalAmount, portfolio = participant.portfolio
            )
        )
        val investment = investmentService.getInvestmentForSymbol(username, contestNumber, symbol)

        assertEquals(symbol, investment!!.symbol)
        assertEquals(totalAmount, investment.amount)
    }

    @Test
    fun `should get remaining funds`() {
        participant.remainingFund = 1400.00
        val remainingFunds = investmentService.getRemainingFunds(username, contestNumber)

        assertEquals(1400.00, remainingFunds)
    }

    private fun verifyCommonFields(investmentOrder: InvestmentOrder) {
        assertTrue(participant.investmentOrders.size == 1)
        assertEquals(symbol, investmentOrder.symbol)
        assertEquals(acceptedPrice, investmentOrder.acceptedPrice)
        assertEquals(expirationTime.atStartOfDay(), investmentOrder.expirationTime)
        assertEquals(totalAmount, investmentOrder.totalAmount)
        assertEquals(totalAmount, investmentOrder.remainingAmount)
        assertSame(participant, investmentOrder.participant)
    }

    private fun createInvestmentTransactionRequest() =
        InvestmentTransactionRequest(
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
