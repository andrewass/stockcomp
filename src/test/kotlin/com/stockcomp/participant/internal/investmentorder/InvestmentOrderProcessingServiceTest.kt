package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.internal.ParticipantService
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

class InvestmentOrderProcessingServiceTest {
    private val symbolService = mockk<SymbolServiceExternal>()
    private val participantService = mockk<ParticipantService>()
    private val investmentOrderProcessingTransactions = mockk<InvestmentOrderProcessingTransactions>()
    private val service =
        InvestmentOrderProcessingService(
            symbolService = symbolService,
            participantService = participantService,
            investmentOrderProcessingTransactions = investmentOrderProcessingTransactions,
        )

    @Test
    fun `should fetch prices before locked order mutation`() {
        every { investmentOrderProcessingTransactions.getActiveInvestmentOrderSymbols(PARTICIPANT_ID) } returns setOf(SYMBOL)
        every { symbolService.getCurrentPrice(SYMBOL) } returns currentPrice(SYMBOL, "90.00")
        every { investmentOrderProcessingTransactions.processActiveInvestmentOrders(PARTICIPANT_ID, any()) } just Runs

        service.processInvestmentOrders(PARTICIPANT_ID)

        verifyOrder {
            investmentOrderProcessingTransactions.getActiveInvestmentOrderSymbols(PARTICIPANT_ID)
            symbolService.getCurrentPrice(SYMBOL)
            investmentOrderProcessingTransactions.processActiveInvestmentOrders(PARTICIPANT_ID, any())
        }
    }

    @Test
    fun `should not mutate orders when price lookup fails`() {
        every { investmentOrderProcessingTransactions.getActiveInvestmentOrderSymbols(PARTICIPANT_ID) } returns setOf(SYMBOL)
        every { symbolService.getCurrentPrice(SYMBOL) } throws FastFinanceClientException("FastFinance failed")

        assertThrows(FastFinanceClientException::class.java) {
            service.processInvestmentOrders(PARTICIPANT_ID)
        }

        verify(exactly = 0) {
            investmentOrderProcessingTransactions.processActiveInvestmentOrders(any(), any())
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
