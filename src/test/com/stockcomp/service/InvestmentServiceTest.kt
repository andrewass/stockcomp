package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.User
import com.stockcomp.entity.contest.Contest
import com.stockcomp.entity.contest.Participant
import com.stockcomp.exception.InsufficientFundsException
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.RealTimePriceResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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

    @MockK
    private lateinit var stockConsumer: StockConsumer

    @InjectMockKs
    private lateinit var investmentService: InvestmentService

    private val username = "testUser"
    private val contestNumber = 100
    private val symbol = "AAPL"
    private val contest = createContest()
    private val participant = createParticipant()
    private val realTimePriceResponse = createRealTimePriceResponse()

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
            stockConsumer.findRealTimePrice(symbol)
        } returns realTimePriceResponse

        every {
            participantRepository.save(participant)
        } returns participant
    }

    @BeforeEach
    private fun resetParticipant() {
        participant.remainingFund = 20000.00
        participant.transactions.clear()
        participant.portfolio.investments.clear()
    }

    @Test
    fun `should buy investment and update portfolio`() {
        val request = InvestmentTransactionRequest(contestNumber, symbol, 20)

        val transactionDto = investmentService.buyInvestment(request, username)
        val investment = participant.portfolio.investments[0]

        assertEquals(symbol, transactionDto.symbol)
        assertEquals(20, transactionDto.amount)
        assertEquals(transactionDto.currentPrice, 120.00)
        assertEquals(participant.transactions.size, 1)
        assertEquals(participant.transactions[0].symbol, transactionDto.symbol)
        assertEquals(participant.transactions[0].amount, transactionDto.amount)
        assertEquals(participant.transactions[0].currentPrice, transactionDto.currentPrice)
        assertEquals(participant.portfolio.investments.size, 1)
        assertEquals(investment.amount, 20)
        assertEquals(investment.symbol, symbol)
    }

    @Test
    fun `should throw exception when buying with insufficient funds`() {
        val request = InvestmentTransactionRequest(contestNumber, symbol, 1000)

        assertThrows(InsufficientFundsException::class.java) {
            investmentService.buyInvestment(request, username)
        }
    }

    @Test
    fun `should sell investment and update portfolio`() {
        val buyRequest = InvestmentTransactionRequest(contestNumber, symbol, 16)
        investmentService.buyInvestment(buyRequest, username)
        val sellRequest = InvestmentTransactionRequest(contestNumber, symbol, 10)

        val transactionDto = investmentService.sellInvestment(sellRequest, username)
        val investment = participant.portfolio.investments[0]

        assertEquals(symbol, transactionDto.symbol)
        assertEquals(10, transactionDto.amount)
        assertEquals(transactionDto.currentPrice, 120.00)
        assertEquals(participant.transactions.size, 2)
        assertEquals(participant.transactions[1].symbol, transactionDto.symbol)
        assertEquals(participant.transactions[1].amount, transactionDto.amount)
        assertEquals(participant.transactions[1].currentPrice, transactionDto.currentPrice)
        assertEquals(participant.portfolio.investments.size, 1)
        assertEquals(investment.amount, 6)
        assertEquals(investment.symbol, symbol)
    }

    @Test
    fun `should throw exception when selling with insufficient funds`() {
        val buyRequest = InvestmentTransactionRequest(contestNumber, symbol, 16)
        investmentService.buyInvestment(buyRequest, username)
        val sellRequest = InvestmentTransactionRequest(contestNumber, symbol, 20)

        assertThrows(InsufficientFundsException::class.java) {
            investmentService.sellInvestment(sellRequest, username)
        }
    }

    @Test
    fun `should throw exception when selling non existing investment`() {
        val request = InvestmentTransactionRequest(contestNumber, symbol, 20)

        assertThrows(InsufficientFundsException::class.java) {
            investmentService.sellInvestment(request, username)
        }
    }

    @Test
    fun `should get investment for a given symbol`() {
        val request = InvestmentTransactionRequest(contestNumber, symbol, 16)
        investmentService.buyInvestment(request, username)

        val investmentDto = investmentService.getInvestmentForSymbol(username, contestNumber, symbol)

        assertEquals(symbol, investmentDto.symbol)
        assertEquals(16, investmentDto.amount)
    }

    @Test
    fun `should get remaining funds`() {
        val request = InvestmentTransactionRequest(contestNumber, symbol, 100)
        investmentService.buyInvestment(request, username)

        val remainingFunds = investmentService.getRemaingFunds(username, contestNumber)

        assertEquals(8000.00, remainingFunds)
    }

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

    private fun createRealTimePriceResponse() =
        RealTimePriceResponse(
            highPrice = 150.00,
            lowPrice = 50.00,
            openPrice = 90.00,
            previousClosePrice = 90.00,
            currentPrice = 120.00,
            time = LocalDateTime.now()
        )
}
