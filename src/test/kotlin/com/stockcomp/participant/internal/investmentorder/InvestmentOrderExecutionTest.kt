package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.OrderStatus
import com.stockcomp.participant.TransactionType
import com.stockcomp.participant.internal.Participant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class InvestmentOrderExecutionTest {
    @Test
    fun `should append an execution for each partial fill`() {
        val participant = Participant(participantId = 1L, userId = 1L, contestId = 1L)
        participant.updateParticipantWhenBuying(amount = 10, symbol = "MSFT", currentPrice = BigDecimal("100.00"))
        val order =
            InvestmentOrder(
                participant = participant,
                currency = "USD",
                acceptedPrice = BigDecimal("100.00"),
                expirationTime = Instant.now().plus(Duration.ofDays(1)),
                symbol = "AAPL",
                totalAmount = 200,
                transactionType = TransactionType.BUY,
            )
        participant.addInvestmentOrder(order)

        order.processOrder(BigDecimal("100.00"))
        participant.updateParticipantWhenSelling(amount = 10, symbol = "MSFT", currentPrice = BigDecimal("100.00"))
        order.processOrder(BigDecimal("100.00"))

        assertEquals(OrderStatus.COMPLETED, order.orderStatus)
        assertEquals(0, order.remainingAmount)
        assertEquals(listOf(190, 10), order.executions().map { it.amount })
        assertEquals(
            listOf(BigDecimal("100.00"), BigDecimal("100.00")),
            order.executions().map { it.executionPrice },
        )
    }
}
