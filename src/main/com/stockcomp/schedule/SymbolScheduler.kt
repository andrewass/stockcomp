package com.stockcomp.schedule

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.Exchange
import com.stockcomp.document.SymbolDocument
import com.stockcomp.repository.document.SymbolDocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SymbolScheduler(
    private val stockConsumer: StockConsumer,
    private val repository: SymbolDocumentRepository
) {
    private val logger = LoggerFactory.getLogger(SymbolScheduler::class.java)

    @Scheduled(fixedRateString = "\${schedule.rate}")
    fun populateSymbols() {
        logger.info("Populating symbols for ElasticSearch")
        val stockExchanges = findStockExchanges()
        updatePersistedSymbolDocuments(fetchSymbolsFromStockExchanges(stockExchanges))
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

    private fun fetchSymbolsFromStockExchanges(stockExchanges: List<Exchange>): List<SymbolDocument> {
        val symbols = ArrayList<SymbolDocument>()
        for (exchange in stockExchanges) {
            val exchangeSymbols = stockConsumer.findAllSymbolsForExchange(exchange.exchangeCode)
            for (symbol in exchangeSymbols) {
                symbol.exchange = exchange
                symbols.add(symbol)
            }
            Thread.sleep(1500L)
        }
        return symbols
    }

    private fun updatePersistedSymbolDocuments(symbols: List<SymbolDocument>) {
        repository.deleteAll()
        repository.saveAll(symbols)
        logger.info("Number of SymbolDocument fetched and persisted : " + repository.count())
    }
}