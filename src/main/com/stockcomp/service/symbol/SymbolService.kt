package com.stockcomp.service.symbol

import com.stockcomp.consumer.QuoteConsumer
import com.stockcomp.response.RealTimePrice
import org.springframework.stereotype.Service

@Service
class SymbolService(
    private val quoteConsumer: QuoteConsumer
) {

    fun getRealTimePrice(symbol: String): RealTimePrice {
        return quoteConsumer.getRealTimePrice(symbol)
    }
}