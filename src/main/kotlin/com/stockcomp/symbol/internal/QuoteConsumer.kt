package com.stockcomp.symbol.internal

import com.stockcomp.symbol.dto.CurrentPriceSymbolDto

interface QuoteConsumer {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto

    fun getCurrentPriceTrendingSymbols(symbols: List<String>): List<CurrentPriceSymbolDto>
}
