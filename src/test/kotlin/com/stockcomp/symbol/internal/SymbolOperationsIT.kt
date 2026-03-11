package com.stockcomp.symbol.internal

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import com.stockcomp.configuration.ControllerIntegrationTest
import com.stockcomp.configuration.mockMvcGetRequest
import com.stockcomp.symbol.CurrentPriceSymbolDto
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrationTest
class SymbolOperationsIT
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @MockkBean
        private lateinit var quoteConsumer: QuoteConsumer

        private val mapper = jacksonObjectMapper()
        private val basePath = "/symbols"

        @Test
        fun `should return trending symbols with current prices`() {
            val symbols =
                listOf(
                    CurrentPriceSymbolDto(
                        symbol = "AAPL",
                        companyName = "Apple",
                        currentPrice = 190.5,
                        previousClose = 189.0,
                        currency = "USD",
                        percentageChange = 0.8,
                        usdPrice = 190.5,
                    ),
                )

            every { quoteConsumer.getCurrentPriceTrendingSymbols(any()) } returns symbols

            val result =
                mockMvc
                    .perform(mockMvcGetRequest("$basePath/price/trending"))
                    .andExpect(status().isOk)
                    .andReturn()

            val response: SymbolController.TrendingSymbolsResponse = mapper.readValue(result.response.contentAsString)
            assertTrue(response.symbols.isNotEmpty())
            assertEquals("AAPL", response.symbols.first().symbol)

            verify { quoteConsumer.getCurrentPriceTrendingSymbols(any()) }
        }
    }
