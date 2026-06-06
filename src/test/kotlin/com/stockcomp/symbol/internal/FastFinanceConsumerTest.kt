package com.stockcomp.symbol.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class FastFinanceConsumerTest {
    @Test
    fun `should map current price response from FastFinance contract`() {
        val webClient =
            WebClient
                .builder()
                .exchangeFunction { request ->
                    assertEquals("/price/current-price/AAPL", request.url().path)

                    Mono.just(
                        ClientResponse
                            .create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(
                                """
                                {
                                  "symbol": "AAPL",
                                  "companyName": "Apple",
                                  "currentPrice": 190.5,
                                  "previousClose": 189.0,
                                  "currency": "USD"
                                }
                                """.trimIndent(),
                            ).build(),
                    )
                }.build()
        val consumer = FastFinanceConsumer(webClient)
        ReflectionTestUtils.setField(consumer, "baseUrl", "http://fastfinance.test")

        val currentPrice = consumer.getCurrentPrice("AAPL")

        assertEquals("AAPL", currentPrice.symbol)
        assertEquals("Apple", currentPrice.companyName)
        assertEquals(190.5, currentPrice.currentPrice)
        assertEquals(189.0, currentPrice.previousClose)
        assertEquals("USD", currentPrice.currency)
        assertEquals(0.7936507936507936, currentPrice.percentageChange)
        assertEquals(190.5, currentPrice.usdPrice)
    }
}
