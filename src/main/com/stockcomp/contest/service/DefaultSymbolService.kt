package com.stockcomp.contest.service

import com.stockcomp.contest.consumer.QuoteConsumer
import com.stockcomp.contest.dto.RealTimePrice
import org.springframework.stereotype.Service

@Service
class DefaultSymbolService(
    private val quoteConsumer: QuoteConsumer
) : SymbolService {

    override fun getRealTimePrice(symbol: String): RealTimePrice {
        return quoteConsumer.getRealTimePrice(symbol)
    }
}