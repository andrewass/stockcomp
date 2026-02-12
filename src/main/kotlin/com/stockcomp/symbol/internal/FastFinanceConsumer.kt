package com.stockcomp.symbol.internal

import com.stockcomp.symbol.CurrentPriceSymbolDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Component("fastfinance.quote.consumer")
class FastFinanceConsumer(
    private val webClient: WebClient,
) : QuoteConsumer {
    @Value("\${fastfinance.base.url}")
    private lateinit var baseUrl: String

    override fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto =
        webClient
            .get()
            .uri(URI("$baseUrl/price/current/$symbol"))
            .retrieve()
            .bodyToMono<CurrentPriceSymbolDto>()
            .block()!!

    override fun getCurrentPriceTrendingSymbols(symbols: List<String>): List<CurrentPriceSymbolDto> =
        webClient
            .post()
            .uri(URI("$baseUrl/price/symbols"))
            .bodyValue(TrendingSymbolsRequest(symbols))
            .retrieve()
            .bodyToMono<List<CurrentPriceSymbolResponse>>()
            .block()!!
            .map { it.toCurrentPriceSymbolDto() }

    private data class TrendingSymbolsRequest(
        val symbols: List<String>,
    )

    private data class CurrentPriceSymbolResponse(
        val symbol: String,
        val companyName: String,
        val currentPrice: Double,
        val previousClose: Double,
        val currency: String,
    )

    private fun CurrentPriceSymbolResponse.toCurrentPriceSymbolDto(): CurrentPriceSymbolDto =
        CurrentPriceSymbolDto(
            symbol = symbol,
            companyName = companyName,
            currentPrice = currentPrice,
            previousClose = previousClose,
            currency = currency,
            percentageChange = (currentPrice - previousClose) / previousClose * 100.0,
            usdPrice = currentPrice,
        )
}
