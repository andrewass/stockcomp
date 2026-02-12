package com.stockcomp.symbol

import com.stockcomp.symbol.internal.QuoteConsumer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SymbolServiceExternal(
    @param:Qualifier("fastfinance.quote.consumer") private val quoteConsumer: QuoteConsumer,
) {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto = quoteConsumer.getCurrentPrice(symbol)
}
