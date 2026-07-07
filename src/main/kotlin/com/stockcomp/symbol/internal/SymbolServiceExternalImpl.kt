package com.stockcomp.symbol.internal

import com.stockcomp.symbol.CurrentPriceSymbolDto
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SymbolServiceExternalImpl(
    @param:Qualifier("fastfinance.quote.consumer") private val quoteConsumer: QuoteConsumer,
) : SymbolServiceExternal {
    override fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto = quoteConsumer.getCurrentPrice(symbol)
}
