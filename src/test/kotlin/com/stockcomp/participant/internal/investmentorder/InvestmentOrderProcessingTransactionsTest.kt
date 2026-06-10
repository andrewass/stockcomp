package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.OrderStatus
import com.stockcomp.participant.TransactionType
import com.stockcomp.participant.internal.Participant
import com.stockcomp.participant.internal.ParticipantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class InvestmentOrderProcessingTransactionsTest {
    private val participantRepository = mockk<ParticipantRepository>()
    private val transactions = InvestmentOrderProcessingTransactions(participantRepository)

    @Test
    fun `should not process the same completed order twice`() {
        val participant = Participant(participantId = PARTICIPANT_ID, userId = 1L, contestId = 1L)
        val order =
            InvestmentOrder(
                participant = participant,
                currency = "USD",
                acceptedPrice = BigDecimal("100.00"),
                expirationTime = LocalDateTime.now().plusDays(1),
                symbol = SYMBOL,
                totalAmount = 2,
                transactionType = TransactionType.BUY,
            )
        participant.addInvestmentOrder(order)

        every { participantRepository.findByIdLocked(PARTICIPANT_ID) } returns participant
        every { participantRepository.save(participant) } returns participant

        transactions.processActiveInvestmentOrders(
            participantId = PARTICIPANT_ID,
            pricesBySymbol = mapOf(SYMBOL to BigDecimal("90.00")),
        )
        val remainingFundsAfterFirstPass = participant.remainingFunds()

        transactions.processActiveInvestmentOrders(
            participantId = PARTICIPANT_ID,
            pricesBySymbol = mapOf(SYMBOL to BigDecimal("90.00")),
        )

        assertEquals(OrderStatus.COMPLETED, order.orderStatus)
        assertEquals(0, order.remainingAmount)
        assertBigDecimalEquals("19820.00", participant.remainingFunds())
        assertBigDecimalEquals("19820.00", remainingFundsAfterFirstPass)
        verify(exactly = 2) { participantRepository.findByIdLocked(PARTICIPANT_ID) }
    }

    private fun assertBigDecimalEquals(
        expected: String,
        actual: BigDecimal,
    ) {
        assertEquals(0, BigDecimal(expected).compareTo(actual))
    }

    private companion object {
        const val PARTICIPANT_ID = 1L
        const val SYMBOL = "AAPL"
    }
}
