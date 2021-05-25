package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPrice
import com.stockcomp.response.RealTimePrice
import com.stockcomp.response.SymbolSearch
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StockService(
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

    fun searchSymbol(query: String): List<SymbolSearch> {
        return stockConsumer.searchSymbol(query)
    }

    fun getHistoricPriceList(symbol: String): List<HistoricPrice> {
        return stockConsumer.getHistoricPriceList(symbol)
    }

    fun getRealTimePrice(symbol: String): RealTimePrice {
        return stockConsumer.findRealTimePrice(symbol)
    }
}
