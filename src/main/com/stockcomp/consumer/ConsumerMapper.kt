package com.stockcomp.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPriceResponse
import com.stockcomp.response.SymbolSearchResponse
import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.min

object ConsumerMapper {

    private val mapper = ObjectMapper()

    fun mapToHistoricPrices(result: JsonNode): List<HistoricPriceResponse> {
        val closingPrices = result.get("c").toString()
        val dates = result.get("t").toString()

        val mappedPrices = mapper.readValue(closingPrices, object : TypeReference<List<Double>>() {})
        val mappedDates = mapper.readValue(dates, object : TypeReference<List<Long>>() {})
            .map { Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).toLocalDate() }

        val length = min(mappedPrices.size, mappedDates.size)
        val results = mutableListOf<HistoricPriceResponse>()

        for (i in 0 until length) {
            results.add(HistoricPriceResponse(date = mappedDates[i], price = mappedPrices[i]))
        }

        return results
    }

    fun mapToSymbolDocuments(result: JsonNode): List<SymbolDocument> {
        return mapper.readerFor(object : TypeReference<List<SymbolDocument>>() {}).readValue(result)
    }

    fun mapToSymbolSearchResponseList(result: JsonNode): List<SymbolSearchResponse> {
        return mapper.readerFor(object : TypeReference<List<SymbolSearchResponse>>() {}).readValue(result)
    }
}