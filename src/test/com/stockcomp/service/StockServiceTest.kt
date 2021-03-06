package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPrice
import com.stockcomp.response.RealTimePrice
import com.stockcomp.response.SymbolSearch
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.data.elasticsearch.core.TotalHitsRelation
import org.springframework.data.elasticsearch.core.query.Query
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StockServiceTest {

    @MockK
    private lateinit var stockConsumer: StockConsumer

    @MockK
    private lateinit var operations: ElasticsearchOperations

    @InjectMockKs
    private lateinit var symbolService: SymbolService

    private val symbol1 = "AAPL"
    private val symbol2 = "APPL"
    private val description1 = "APPLE INC"
    private val description2 = "Another Apple"
    private val query = "apple"

    @BeforeAll
    private fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should get real time prices for given symbol`() {
        val consumerResponse = createRealTimePrice()
        every {
            stockConsumer.findRealTimePrice(symbol1)
        } returns consumerResponse

        val response = symbolService.getRealTimePrice(symbol1)

        assertEquals(consumerResponse.currentPrice, response.currentPrice)
        assertEquals(consumerResponse.highPrice, response.highPrice)
        assertEquals(consumerResponse.lowPrice, response.lowPrice)
        assertEquals(consumerResponse.openPrice, response.openPrice)
        assertEquals(consumerResponse.previousClosePrice, response.previousClosePrice)
        assertEquals(consumerResponse.time, response.time)
    }

    @Test
    fun `should get historic price list for given symbol`() {
        val consumerResponse = createHistoricPriceList()
        every {
            stockConsumer.getHistoricPriceList(symbol1)
        } returns consumerResponse

        val response = symbolService.getHistoricPriceList(symbol1)

        assertEquals(2, response.size)
        assertEquals(consumerResponse[0].price, response[0].price)
        assertEquals(consumerResponse[0].date, response[0].date)
        assertEquals(consumerResponse[1].price, response[1].price)
        assertEquals(consumerResponse[1].date, response[1].date)
    }

    @Test
    fun `should get empty list when historic list not found`() {
        every {
            stockConsumer.getHistoricPriceList(symbol1)
        } returns emptyList()

        val response = symbolService.getHistoricPriceList(symbol1)

        assertTrue(response.isEmpty())
    }

    @Test
    fun `should get symbol search list for given query`() {
        val consumerResponse = createSymbolSearchList()
        every {
            stockConsumer.searchSymbol(query)
        } returns consumerResponse

        val response = symbolService.searchSymbol(query)

        assertEquals(2, response.size)
        assertEquals(consumerResponse[0].description, response[0].description)
        assertEquals(consumerResponse[0].symbol, response[0].symbol)
        assertEquals(consumerResponse[1].description, response[1].description)
        assertEquals(consumerResponse[1].symbol, response[1].symbol)
    }

    @Test
    fun `should get empty list when symbol search gives no match`() {
        every {
            stockConsumer.searchSymbol(query)
        } returns emptyList()

        val response = symbolService.searchSymbol(query)

        assertTrue(response.isEmpty())
    }

    @Test
    fun `should get symbol suggestions for given query`() {
        every {
            operations.search(any<Query>(), SymbolDocument::class.java)
        } returns createSearchHits()

        val response = symbolService.getSymbolSuggestions(query)

        assertEquals(description1, response[0].description)
        assertEquals(symbol1, response[0].symbol)
        assertEquals(description2, response[1].description)
        assertEquals(symbol2, response[1].symbol)
    }

    @Test
    fun `should get empty list when no suggestions`() {
        every {
            operations.search(any<Query>(), SymbolDocument::class.java)
        } returns createEmptySearchHits()

        val response = symbolService.getSymbolSuggestions(query)

        verify { operations.search(any<Query>(), SymbolDocument::class.java) }
        assertTrue(response.isEmpty())
    }

    private fun createSearchHits() =
        SearchHitsImpl<SymbolDocument>(
            2L, TotalHitsRelation.EQUAL_TO,
            4f, null,
            listOf(
                SearchHit(null, null, 4f,
                    null, null,
                    SymbolDocument().apply {
                        symbol = symbol1
                        description = description1
                    }),
                SearchHit(null, null, 4f,
                    null, null,
                    SymbolDocument().apply {
                        symbol = symbol2
                        description = description2
                    })
            ), null
        )

    private fun createEmptySearchHits() =
        SearchHitsImpl<SymbolDocument>(
            2L, TotalHitsRelation.EQUAL_TO,
            4f, null, emptyList(), null
        )


    private fun createSymbolSearchList() =
        listOf(
            SymbolSearch(symbol = symbol1, description = description1),
            SymbolSearch(symbol = symbol2, description = description2)
        )

    private fun createHistoricPriceList() =
        listOf(
            HistoricPrice(price = 124.00, date = LocalDate.now()),
            HistoricPrice(price = 122.00, date = LocalDate.now().minusDays(1))
        )

    private fun createRealTimePrice() =
        RealTimePrice(
            currentPrice = 155.00,
            previousClosePrice = 140.00,
            openPrice = 140.00,
            lowPrice = 134.00,
            highPrice = 160.00,
            time = LocalDateTime.now()
        )
}