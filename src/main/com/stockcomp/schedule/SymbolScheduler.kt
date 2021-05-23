package com.stockcomp.schedule

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.Exchange
import com.stockcomp.document.SymbolDocument
import com.stockcomp.repository.document.SymbolDocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

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

    private fun findStockExchanges(): List<Exchange> {
        val streamReader = InputStreamReader(this.javaClass.getResourceAsStream("/domain/stockexchanges.csv"))
        val bufferedReader = BufferedReader(streamReader)
        val exchanges = ArrayList<Exchange>()
        var row: String
        while (bufferedReader.readLine().also { row = it } != null) {
            val data = row.split(",".toRegex()).toTypedArray()
            exchanges.add(Exchange(data[0], data[1]))
        }
        bufferedReader.close()

        return exchanges
    }

    private fun updatePersistedSymbolDocuments(symbols: List<SymbolDocument>) {
        repository.deleteAll()
        repository.saveAll(symbols)
        logger.info("Number of SymbolDocument fetched and persisted : " + repository.count())
    }
}