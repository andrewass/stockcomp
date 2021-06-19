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
import java.util.stream.Stream

@Service
class SymbolService(
    private val stockConsumer: StockConsumer,
    private val operations: ElasticsearchOperations
) {

    fun getSymbolSuggestions(query: String): List<SymbolDocument> {
        val descriptionQuery = NativeSearchQueryBuilder()
            .withFilter(QueryBuilders.regexpQuery("description", ".*$query.*"))
            .withPageable(PageRequest.of(0, 10))
            .build()

        val symbolQuery = NativeSearchQueryBuilder()
            .withFilter(QueryBuilders.regexpQuery("symbol", ".*$query.*"))
            .withPageable(PageRequest.of(0, 10))
            .build()

        val descriptionHits = operations.search(descriptionQuery, SymbolDocument::class.java)
        val symbolHits = operations.search(symbolQuery, SymbolDocument::class.java)

        return Stream.concat(descriptionHits.get(), symbolHits.get())
            .map { it.content }
            .distinct()
            .collect(Collectors.toList())
    }

    fun searchSymbol(query: String): List<SymbolSearch> =
        stockConsumer.searchSymbol(query)

    fun getHistoricPriceList(symbol: String): List<HistoricPrice> =
        stockConsumer.getHistoricPriceList(symbol)

    fun getRealTimePrice(symbol: String): RealTimePrice =
        stockConsumer.findRealTimePrice(symbol)
}
