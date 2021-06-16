package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.Exchange
import com.stockcomp.document.SymbolDocument
import com.stockcomp.repository.document.SymbolDocumentRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SymbolSearchService(
    private val stockConsumer: StockConsumer,
    private val repository: SymbolDocumentRepository
) {

    @Value("\${auto.start.tasks}")
    private val autoStartTasks: Boolean = false

    private val logger = LoggerFactory.getLogger(SymbolSearchService::class.java)

    init {
        if(autoStartTasks) {
            GlobalScope.launch {
                populateSymbols()
            }
        }
    }

    suspend fun populateSymbols() {
        while (true) {
            logger.info("Populating symbols for ElasticSearch")
            val stockExchanges = findStockExchanges()
            updatePersistedSymbolDocuments(fetchSymbolsFromStockExchanges(stockExchanges))
            delay(60000 * 60)
        }
    }

    private fun findStockExchanges(): List<Exchange> {
        return this::class.java.getResourceAsStream("/domain/stockexchanges.csv")
            .bufferedReader()
            .readLines()
            .map { mapToExchange(it) }
    }

    private fun mapToExchange(line: String): Exchange {
        val values = line.split(",")

        return Exchange(values[0], values[1])
    }

    private suspend fun fetchSymbolsFromStockExchanges(stockExchanges: List<Exchange>): List<SymbolDocument> {
        val symbols = ArrayList<SymbolDocument>()
        for (exchange in stockExchanges) {
            val exchangeSymbols = stockConsumer.findAllSymbolsForExchange(exchange.exchangeCode)
            for (symbol in exchangeSymbols) {
                symbol.exchange = exchange
                symbols.add(symbol)
            }
            delay(1500L)
        }
        return symbols
    }

    private fun updatePersistedSymbolDocuments(symbols: List<SymbolDocument>) {
        repository.deleteAll()
        repository.saveAll(symbols)
        logger.info("Number of SymbolDocument fetched and persisted : " + repository.count())
    }
}