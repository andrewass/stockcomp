package com.stockcomp.contest.service

import com.stockcomp.contest.dto.RealTimePrice
import com.stockcomp.price.consumer.QuoteConsumer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class DefaultSymbolService(
    @Qualifier("fastfinance.quote.consumer") private val quoteConsumer: QuoteConsumer
) : SymbolService {

    override fun getRealTimePrice(symbol: String): RealTimePrice {
        return quoteConsumer.getRealTimePrice(symbol)
    }
}