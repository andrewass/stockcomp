package com.stockcomp.participant.internal.investment

import com.stockcomp.participant.internal.ParticipantRepository
import com.stockcomp.symbol.CurrentPriceSymbolDto
import com.stockcomp.symbol.SymbolServiceExternal
import com.stockcomp.symbol.internal.FastFinanceClientException
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class InvestmentProcessingServiceTest {
    private val participantRepository = mockk<ParticipantRepository>()
    private val investmentProcessingTransactions = mockk<InvestmentProcessingTransactions>()
    private val symbolService = mockk<SymbolServiceExternal>()
    private val service =
        InvestmentProcessingService(
            participantRepository = participantRepository,
            investmentProcessingTransactions = investmentProcessingTransactions,
            symbolService = symbolService,
        )

    @Test
    fun `should fetch prices before locked investment mutation`() {
        every { investmentProcessingTransactions.getInvestmentSymbols(PARTICIPANT_ID) } returns setOf(SYMBOL)
        every { symbolService.getCurrentPrice(SYMBOL) } returns currentPrice(SYMBOL, "125.00")
        every { investmentProcessingTransactions.maintainInvestments(PARTICIPANT_ID, any()) } just Runs

        service.maintainInvestments(PARTICIPANT_ID)

        verifyOrder {
            investmentProcessingTransactions.getInvestmentSymbols(PARTICIPANT_ID)
            symbolService.getCurrentPrice(SYMBOL)
            investmentProcessingTransactions.maintainInvestments(PARTICIPANT_ID, any())
        }
    }

    @Test
    fun `should not mutate investments when price lookup fails`() {
        every { investmentProcessingTransactions.getInvestmentSymbols(PARTICIPANT_ID) } returns setOf(SYMBOL)
        every { symbolService.getCurrentPrice(SYMBOL) } throws FastFinanceClientException("FastFinance failed")

        assertThrows(FastFinanceClientException::class.java) {
            service.maintainInvestments(PARTICIPANT_ID)
        }

        verify(exactly = 0) {
            investmentProcessingTransactions.maintainInvestments(any(), any())
        }
    }

    private fun currentPrice(
        symbol: String,
        price: String,
    ) = CurrentPriceSymbolDto(
        symbol = symbol,
        companyName = symbol,
        currentPrice = BigDecimal(price),
        previousClose = BigDecimal(price),
        currency = "USD",
        percentageChange = BigDecimal.ZERO,
        usdPrice = BigDecimal(price),
    )

    private companion object {
        const val PARTICIPANT_ID = 1L
        const val SYMBOL = "AAPL"
    }
}
