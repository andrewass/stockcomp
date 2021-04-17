package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPriceResponse
import com.stockcomp.response.RealTimePriceResponse
import com.stockcomp.response.SymbolSearchResponse
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StockService @Autowired constructor(
    private val stockConsumer: StockConsumer,
    private val operations: ElasticsearchOperations
) {

    fun getSymbolSuggestions(symbolQuery: String): List<SymbolDocument> {
        val query = NativeSearchQueryBuilder()
            .withFilter(QueryBuilders.regexpQuery("description", ".*$symbolQuery.*"))
            .withPageable(PageRequest.of(0, 10))
            .build()

        val searchHits = operations.search(query, SymbolDocument::class.java)

        return searchHits.get().map { it.content }.collect(Collectors.toList())
    }

    fun searchSymbol(symbol: String): List<SymbolSearchResponse> {
        return stockConsumer.searchSymbol(symbol)
    }

    fun getHistoricPriceList(symbol: String): List<HistoricPriceResponse> {
        return stockConsumer.getHistoricPriceList(symbol)
    }

    fun getRealTimePrice(symbol: String): RealTimePriceResponse {
        return stockConsumer.findRealTimePrice(symbol)
    }
}
