package com.stockcomp.symbol.internal

import com.stockcomp.symbol.dto.CurrentPriceSymbolDto
import org.springframework.stereotype.Service

@Service
class SymbolServiceInternal(
    private val quoteConsumer: QuoteConsumer,
) {
    private val defaultSymbols =
        listOf(
            "AAPL",
            "GOOG",
            "MSFT",
            "AMZN",
            "META",
            "TSLA",
            "PLTR",
            "AMD",
            "INTC",
            "NVDA",
        )

    fun getCurrentPriceTrendingSymbols(): List<CurrentPriceSymbolDto> = quoteConsumer.getCurrentPriceTrendingSymbols(defaultSymbols)
}
