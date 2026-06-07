package com.stockcomp.participant.internal.investment

import com.stockcomp.participant.internal.Participant
import com.stockcomp.participant.internal.ParticipantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class InvestmentProcessingTransactionsTest {
    private val participantRepository = mockk<ParticipantRepository>()
    private val transactions = InvestmentProcessingTransactions(participantRepository)

    @Test
    fun `should re-read locked participant before maintaining investments`() {
        val participant = Participant(participantId = PARTICIPANT_ID, userId = 1L, contestId = 1L)
        participant.updateParticipantWhenBuying(amount = 3, symbol = SYMBOL, currentPrice = BigDecimal("100.00"))
        participant.updateInvestmentValues()

        every { participantRepository.findByIdLocked(PARTICIPANT_ID) } returns participant
        every { participantRepository.save(participant) } returns participant

        transactions.maintainInvestments(
            participantId = PARTICIPANT_ID,
            pricesBySymbol = mapOf(SYMBOL to BigDecimal("125.00")),
        )

        val investment = participant.investments().single()
        assertBigDecimalEquals("375.00", investment.totalValue)
        assertBigDecimalEquals("75.00", investment.totalProfit)
        assertBigDecimalEquals("375.00", participant.totalInvestmentValue())
        assertBigDecimalEquals("20075.00", participant.totalValue())
        verify(exactly = 1) { participantRepository.findByIdLocked(PARTICIPANT_ID) }
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
