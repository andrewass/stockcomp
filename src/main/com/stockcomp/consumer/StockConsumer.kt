package com.stockcomp.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.RealTimePriceResponse
import com.stockcomp.response.SymbolSearchResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Component
class StockConsumer(private val webClient: WebClient) {
    private val finnhubToken = System.getenv("FINNHUB_API_KEY")

    private val mapper: ObjectMapper = ObjectMapper()

    fun findRealTimePrice(symbol: String?): RealTimePriceResponse {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/quote")
                    .queryParam("symbol", symbol)
                    .queryParam("token", finnhubToken).build()
            }
            .retrieve()
            .bodyToMono(RealTimePriceResponse::class.java)
            .block()!!
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

        return convertToSymbolSearchResponseList(result)
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

        return convertToSymbolDocuments(result)
    }

    private fun convertToSymbolDocuments(result: JsonNode): List<SymbolDocument> {
        return mapper.readerFor(object : TypeReference<List<SymbolDocument>>() {}).readValue(result)
    }

    private fun convertToSymbolSearchResponseList(result: JsonNode): List<SymbolSearchResponse> {
        return mapper.readerFor(object : TypeReference<List<SymbolSearchResponse>>() {}).readValue(result)
    }
}