package com.stockcomp.service.symbol

import com.stockcomp.consumer.QuoteConsumer
import org.springframework.stereotype.Service

@Service
class SymbolService(
    private val quoteConsumer: QuoteConsumer
) {

    fun getRealTimePrice(symbol: String): Double {
        return quoteConsumer.getRealTimePrice(symbol).price
    }
}