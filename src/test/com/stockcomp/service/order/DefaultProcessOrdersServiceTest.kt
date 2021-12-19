package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Investment
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.domain.contest.enums.TransactionType.BUY
import com.stockcomp.domain.contest.enums.TransactionType.SELL
import com.stockcomp.domain.user.User
import com.stockcomp.dto.RealTimePrice
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.InvestmentRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.service.symbol.SymbolService
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
internal class DefaultProcessOrdersServiceTest {

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @MockK
    private lateinit var investmentOrderRepository: InvestmentOrderRepository

    @RelaxedMockK
    private lateinit var investmentRepository: InvestmentRepository

    @MockK
    private lateinit var symbolService: SymbolService

    @RelaxedMockK
    private lateinit var meterRegistry: SimpleMeterRegistry

    @InjectMockKs
    private lateinit var processOrdersService: DefaultProcessOrdersService

    private val contest = Contest(
        contestNumber = 1, startTime = LocalDateTime.now(), endTime = LocalDateTime.now().plusWeeks(4)
    )

    private val firstUser = User(username = "firstUser", password = "firstPassword", email = "first@mail.com")
    private val secondUser = User(username = "secUser", password = "secPassword", email = "sec@mail.com")
    private val firstParticipant = Participant(contest = contest, rank = 1, user = firstUser)
    private val secParticipant = Participant(contest = contest, rank = 2, user = secondUser)

    private val AAPL : String = "AAPL";
    private val MSFT  : String = "MSFT"
    private val NFLX : String = "NFLX"

    private val investmentOrders = createInvestmentOrders()

    @BeforeEach
    private fun setup() {
        every {
            symbolService.getRealTimePrice(AAPL)
        } returns RealTimePrice(
            currency = "USD", dayHigh = 150.00, dayLow = 50.00, openPrice = 50.00,
            previousClose = 50.00, price = 110.00, usdPrice = 110.00
        )

        every {
            symbolService.getRealTimePrice(MSFT)
        } returns RealTimePrice(
            currency = "USD", dayHigh = 150.00, dayLow = 50.00, openPrice = 50.00,
            previousClose = 50.00, price = 110.00, usdPrice = 110.00
        )

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
            investmentOrderRepository.findAllByOrderAndContestStatus(OrderStatus.ACTIVE, ContestStatus.RUNNING)
        } returns investmentOrders.subList(0, 4)

        processOrdersService.processInvestmentOrders()

        assertEquals(10,investmentOrders[0].remainingAmount )
        assertEquals(OrderStatus.ACTIVE, investmentOrders[0].orderStatus)

        assertEquals(0, investmentOrders[1].remainingAmount )
        assertEquals(OrderStatus.COMPLETED, investmentOrders[1].orderStatus)

        assertEquals(0, investmentOrders[2].remainingAmount )
        assertEquals(OrderStatus.COMPLETED, investmentOrders[2].orderStatus)

        assertEquals(10, investmentOrders[3].remainingAmount )
        assertEquals(OrderStatus.ACTIVE, investmentOrders[3].orderStatus)

        assertEquals(21100.00, firstParticipant.remainingFund)
        assertEquals(18900.00, secParticipant.remainingFund)
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
                acceptedPrice = 10_0000.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = firstParticipant, symbol = NFLX, totalAmount = 10, transactionType = BUY
            ),
            InvestmentOrder(
                acceptedPrice = 100.00, currency = "USD", expirationTime = LocalDateTime.now().plusWeeks(1),
                participant = firstParticipant, symbol = NFLX, totalAmount = 10, transactionType = SELL
            )
        )
}