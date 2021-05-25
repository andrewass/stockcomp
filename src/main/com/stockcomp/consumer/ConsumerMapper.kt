package com.stockcomp.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPrice
import com.stockcomp.response.RealTimePrice
import com.stockcomp.response.SymbolSearch
import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.min


private val mapper = ObjectMapper()

fun mapToHistoricPrices(result: JsonNode): List<HistoricPrice> {
    val closingPrices = result.get("c").toString()
    val dates = result.get("t").toString()

    val mappedPrices = mapper.readValue(closingPrices, object : TypeReference<List<Double>>() {})
    val mappedDates = mapper.readValue(dates, object : TypeReference<List<Long>>() {})
        .map { Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).toLocalDate() }

    val length = min(mappedPrices.size, mappedDates.size)
    val results = mutableListOf<HistoricPrice>()

    for (i in 0 until length) {
        results.add(HistoricPrice(date = mappedDates[i], price = mappedPrices[i]))
    }
    return results
}

fun mapToRealTimePrice(result: JsonNode): RealTimePrice =
    RealTimePrice(
        currentPrice = result.get("c").doubleValue(),
        previousClosePrice = result.get("pc").doubleValue(),
        openPrice = result.get("o").doubleValue(),
        lowPrice = result.get("l").doubleValue(),
        highPrice = result.get("h").doubleValue(),
        time = Instant.ofEpochSecond(result.get("t").longValue()).atZone(ZoneOffset.UTC).toLocalDateTime()
    )

fun mapToSymbolDocuments(result: JsonNode): List<SymbolDocument> =
    mapper.readerFor(object : TypeReference<List<SymbolDocument>>() {}).readValue(result)

fun mapToSymbolSearchList(result: JsonNode): List<SymbolSearch> =
    mapper.readerFor(object : TypeReference<List<SymbolSearch>>() {}).readValue(result)
