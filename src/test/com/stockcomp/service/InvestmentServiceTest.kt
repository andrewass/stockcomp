package com.stockcomp.service

import com.stockcomp.entity.User
import com.stockcomp.entity.contest.*
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
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
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InvestmentServiceTest {

    @MockK
    private lateinit var contestRepository: ContestRepository

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @InjectMockKs
    private lateinit var investmentService: DefaultInvestmentService

    private val username = "testUser"
    private val contestNumber = 100
    private val acceptedPrice = 150.00
    private val totalAmount  = 120
    private val expirationTime = LocalDateTime.now().plusDays(10L)
    private val symbol = "AAPL"
    private val contest = createContest()
    private val participant = createParticipant()

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
        every {
            contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)
        } returns Optional.of(contest)
        every {
            participantRepository.findParticipantFromUsernameAndContest(username, contest)
        } returns listOf(participant)
        every {
            participantRepository.save(participant)
        } returns participant
    }

    @BeforeEach
    private fun resetData(){
        participant.awaitingOrders.clear()
        participant.remainingFund = 20000.00
    }

    @Test
    fun `should place buy order for participant`() {
        val request = createInvestmentTransactionRequest()

        investmentService.placeBuyOrder(request, username)

        val awaitingOrder = participant.awaitingOrders[0]

        verifyCommonFields(awaitingOrder)
        assertEquals(TransactionType.BUY, awaitingOrder.transactionType)
        verify { participantRepository.save(participant) }
    }

    @Test
    fun `should place sell order for participant`(){
        val request = createInvestmentTransactionRequest()

        investmentService.placeSellOrder(request, username)

        val awaitingOrder = participant.awaitingOrders[0]

        verifyCommonFields(awaitingOrder)
        assertEquals(TransactionType.SELL, awaitingOrder.transactionType)
        verify { participantRepository.save(participant) }
    }

    @Test
    fun `should get investment for a given symbol`() {
        participant.portfolio.investments.add(
            Investment(name = "APPLE INC", symbol = symbol,
                amount = totalAmount, portfolio = participant.portfolio)
        )
        val investment = investmentService.getInvestmentForSymbol(username, contestNumber, symbol)

        assertEquals(symbol, investment.symbol)
        assertEquals(totalAmount, investment.amount)
    }

    @Test
    fun `should get remaining funds`() {
        participant.remainingFund = 1400.00
        val remainingFunds = investmentService.getRemainingFunds(username, contestNumber)

        assertEquals(1400.00, remainingFunds)
    }

    private fun verifyCommonFields(awaitingOrder : AwaitingOrder){
        assertTrue(participant.awaitingOrders.size == 1)
        assertEquals(symbol, awaitingOrder.symbol)
        assertEquals(acceptedPrice, awaitingOrder.acceptedPrice)
        assertEquals(expirationTime, awaitingOrder.expirationTime)
        assertEquals(totalAmount, awaitingOrder.totalAmount)
        assertEquals(totalAmount, awaitingOrder.remainingAmount)
        assertTrue(awaitingOrder.activeOrder)
    }

    private fun createInvestmentTransactionRequest() =
        InvestmentTransactionRequest(
            contestNumber = contestNumber,
            symbol = symbol,
            amount = totalAmount,
            expirationTime = expirationTime,
            acceptedPrice = acceptedPrice
        )

    private fun createContest() =
        Contest(
            contestNumber = 100,
            inPreStartMode = false,
            inRunningMode = true,
            startTime = LocalDateTime.now().minusDays(10)
        )

    private fun createParticipant() =
        Participant(
            user = User(username = username, password = "testPassword", email = "testEmail"),
            contest = contest
        )
}
