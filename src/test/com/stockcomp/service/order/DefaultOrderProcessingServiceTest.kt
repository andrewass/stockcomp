package com.stockcomp.service.order

import com.stockcomp.investmentorder.service.DefaultProcessOrdersService
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.service.symbol.DefaultSymbolService
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DefaultOrderProcessingServiceTest {

    @MockK
    private lateinit var participantRepository: ParticipantRepository

    @MockK
    private lateinit var investmentOrderRepository: InvestmentOrderRepository

    @MockK
    private lateinit var symbolService: DefaultSymbolService

    @InjectMockKs
    private lateinit var maintainOrderService: DefaultProcessOrdersService

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    suspend fun test1() {
        maintainOrderService.processInvestmentOrders()
    }
}