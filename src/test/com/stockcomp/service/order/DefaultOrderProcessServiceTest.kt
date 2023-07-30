package com.stockcomp.service.order

import com.stockcomp.contest.dto.CurrentPriceSymbol
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus.RUNNING
import com.stockcomp.contest.service.SymbolService
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus.*
import com.stockcomp.investmentorder.entity.TransactionType.BUY
import com.stockcomp.investmentorder.entity.TransactionType.SELL
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.investmentorder.service.DefaultInvestmentOrderProcessService
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.entity.User
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultOrderProcessServiceTest {

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @RelaxedMockK
    private lateinit var investmentOrderRepository: InvestmentOrderRepository

    @RelaxedMockK
    private lateinit var investmentRepository: InvestmentRepository

    @MockK
    private lateinit var symbolService: SymbolService

    @RelaxedMockK
    private lateinit var meterRegistry: SimpleMeterRegistry

    @InjectMockKs
    private lateinit var processOrdersService: DefaultInvestmentOrderProcessService

    private val contest = Contest(
        contestNumber = 1, startTime = LocalDateTime.now(), endTime = LocalDateTime.now().plusWeeks(4)
    )

    private val firstUser = User(username = "firstUser", email = "first@mail.com")
    private val secondUser = User(username = "secUser", email = "sec@mail.com")
    private val firstParticipant = Participant(contest = contest, rank = 1, user = firstUser)
    private val secParticipant = Participant(contest = contest, rank = 2, user = secondUser)

    private val AAPL: String = "AAPL"
    private val MSFT: String = "MSFT"

    private val investmentOrders = createInvestmentOrders()

    @BeforeEach
    fun setup() {
        every {
            symbolService.getCurrentPrice(AAPL)
        } returns CurrentPriceSymbol(currency = "USD", currentPrice = 110.00)

        every {
            symbolService.getCurrentPrice(MSFT)
        } returns CurrentPriceSymbol(currency = "USD", currentPrice = 110.00)

        every {
            investmentRepository.findBySymbolAndParticipant(AAPL, firstParticipant)
        } returns Investment(participant = firstParticipant, symbol = AAPL, amount = 10)

        every {
            investmentRepository.findBySymbolAndParticipant(AAPL, secParticipant)
        } returns Investment(participant = secParticipant, symbol = AAPL, amount = 10)

        every {
            investmentRepository.findBySymbolAndParticipant(MSFT, firstParticipant)
        } returns Investment(participant = firstParticipant, symbol = MSFT, amount = 10)

        every {
            investmentRepository.findBySymbolAndParticipant(MSFT, secParticipant)
        } returns Investment(participant = secParticipant, symbol = MSFT, amount = 10)

        every { investmentRepository.save(any()) } returnsArgument 0
        every { investmentOrderRepository.save(any()) } returnsArgument 0
        every { participantRepository.save(any()) } returnsArgument 0
    }

    @Test
    fun `should process active investment orders`() = runTest {
        every {
            investmentOrderRepository.findAllByOrderAndContestStatus(ACTIVE, RUNNING)
        } returns investmentOrders.subList(0, 4)

        processOrdersService.processInvestmentOrders()

        assertEquals(10, investmentOrders[0].remainingAmount)
        assertEquals(ACTIVE, investmentOrders[0].orderStatus)

        assertEquals(0, investmentOrders[1].remainingAmount)
        assertEquals(COMPLETED, investmentOrders[1].orderStatus)

        assertEquals(0, investmentOrders[2].remainingAmount)
        assertEquals(COMPLETED, investmentOrders[2].orderStatus)

        assertEquals(10, investmentOrders[3].remainingAmount)
        assertEquals(ACTIVE, investmentOrders[3].orderStatus)

        assertEquals(21_100.00, firstParticipant.remainingFunds)
        assertEquals(18_900.00, secParticipant.remainingFunds)
    }

    @Test
    fun `should process sell order where current price is above remaining funds`() = runTest {
        every {
            symbolService.getCurrentPrice(AAPL)
        } returns CurrentPriceSymbol(
            currency = "USD", currentPrice = 110_000.00
        )

        every {
            investmentOrderRepository.findAllByOrderAndContestStatus(ACTIVE, RUNNING)
        } returns investmentOrders.subList(4, 5)

        processOrdersService.processInvestmentOrders()

        assertEquals(5, investmentOrders[4].remainingAmount)
        assertEquals(ACTIVE, investmentOrders[4].orderStatus)
        assertEquals(20_000.00, firstParticipant.remainingFunds)
    }

    private fun createInvestmentOrders() =
        listOf(
            InvestmentOrder(
                acceptedPrice = 100.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = firstParticipant, symbol = "AAPL", totalAmount = 10, transactionType = BUY
            ),
            InvestmentOrder(
                acceptedPrice = 120.00, currency = "USD", expirationTime = LocalDateTime.now().minusDays(1),
                participant = secParticipant, symbol = AAPL, totalAmount = 10, transactionType = BUY
            ),
            InvestmentOrder(
                acceptedPrice = 100.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = firstParticipant, symbol = MSFT, totalAmount = 10, transactionType = SELL
            ),
            InvestmentOrder(
                acceptedPrice = 120.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = secParticipant, symbol = MSFT, totalAmount = 10, transactionType = SELL
            ),
            InvestmentOrder(
                acceptedPrice = 120_000.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = firstParticipant, symbol = "AAPL", totalAmount = 5, transactionType = BUY
            )
        )
}