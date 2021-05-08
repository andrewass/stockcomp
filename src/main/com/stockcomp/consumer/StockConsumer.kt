package com.stockcomp.consumer

import com.fasterxml.jackson.databind.JsonNode
import com.stockcomp.document.Exchange
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPriceResponse
import com.stockcomp.response.RealTimePriceResponse
import com.stockcomp.response.SymbolSearchResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class StockConsumer(private val webClient: WebClient) {
    private val finnhubToken = System.getenv("FINNHUB_API_KEY")

    fun findRealTimePrice(symbol: String?): RealTimePriceResponse {
        val result = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/quote")
                    .queryParam("symbol", symbol)
                    .queryParam("token", finnhubToken).build()
            }
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()!!

        return mapToRealTimePriceResponse(result)
    }

    fun searchSymbol(symbol: String): List<SymbolSearchResponse> {
        val result = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/search")
                    .queryParam("q", symbol)
                    .queryParam("token", finnhubToken).build()
            }
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .map { node -> node.path("result") }
            .block()!!

        return mapToSymbolSearchResponseList(result)
    }

    fun findAllSymbolsForExchange(exchange: String): List<SymbolDocument> {
        val result = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/stock/symbol")
                    .queryParam("exchange", exchange)
                    .queryParam("token", finnhubToken).build()
            }
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()!!

        return mapToSymbolDocuments(result).filter { !it.symbol.contains('.') }
    }

    fun getHistoricPriceList(symbol: String): List<HistoricPriceResponse> {
        val currentTimeEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val lastYearEpoch = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC)

        val result = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/stock/candle")
                    .queryParam("symbol", symbol)
                    .queryParam("from", lastYearEpoch)
                    .queryParam("to", currentTimeEpoch)
                    .queryParam("resolution", "D")
                    .queryParam("token", finnhubToken).build()
            }
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()!!

        return mapToHistoricPrices(result)
    }
}
