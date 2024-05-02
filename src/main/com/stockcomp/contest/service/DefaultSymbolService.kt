package com.stockcomp.contest.service

import com.stockcomp.contest.domain.CurrentPriceSymbol
import com.stockcomp.price.consumer.QuoteConsumer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class DefaultSymbolService(
    @Qualifier("fastfinance.quote.consumer") private val quoteConsumer: QuoteConsumer
) : SymbolService {

    override fun getCurrentPrice(symbol: String): CurrentPriceSymbol {
        return quoteConsumer.getCurrentPrice(symbol)
    }
}