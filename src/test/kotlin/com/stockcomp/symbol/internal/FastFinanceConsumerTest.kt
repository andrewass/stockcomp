package com.stockcomp.symbol.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration

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
        val consumer =
            FastFinanceConsumer(
                webClient = webClient,
                baseUrl = "http://fastfinance.test",
                requestTimeout = Duration.ofSeconds(5),
                retryMaxAttempts = 0,
                retryBackoff = Duration.ofMillis(1),
            )

        val currentPrice = consumer.getCurrentPrice("AAPL")

        assertEquals("AAPL", currentPrice.symbol)
        assertEquals("Apple", currentPrice.companyName)
        assertBigDecimalEquals("190.5", currentPrice.currentPrice)
        assertBigDecimalEquals("189.0", currentPrice.previousClose)
        assertEquals("USD", currentPrice.currency)
        assertBigDecimalEquals("0.7936507937", currentPrice.percentageChange)
        assertBigDecimalEquals("190.5", currentPrice.usdPrice)
    }

    private fun assertBigDecimalEquals(
        expected: String,
        actual: BigDecimal,
    ) {
        assertEquals(0, BigDecimal(expected).compareTo(actual))
    }
}
