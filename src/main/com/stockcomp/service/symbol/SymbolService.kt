package com.stockcomp.service.symbol

import com.stockcomp.consumer.QuoteConsumer
import org.springframework.stereotype.Service

@Service
class SymbolService(
    private val quoteConsumer: QuoteConsumer
) {

    fun getRealTimePrice(symbol: String): Double {
        val response = quoteConsumer.getRealTimePrice(symbol)
        return response.price
    }
}