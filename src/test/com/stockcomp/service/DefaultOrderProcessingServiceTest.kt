package com.stockcomp.service

import com.stockcomp.repository.jpa.InvestmentOrderRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.service.order.DefaultOrderProcessingService
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
    private lateinit var stockService: StockService

    @InjectMockKs
    private lateinit var orderProcessingService: DefaultOrderProcessingService

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    suspend fun test1() {
        orderProcessingService.startOrderProcessing()
    }
}