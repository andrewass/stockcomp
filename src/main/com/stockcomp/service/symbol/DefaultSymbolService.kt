package com.stockcomp.service.symbol

import com.stockcomp.consumer.QuoteConsumer
import com.stockcomp.response.RealTimePrice
import org.springframework.stereotype.Service

@Service
class DefaultSymbolService(
    private val quoteConsumer: QuoteConsumer
) : SymbolService {

    override fun getRealTimePrice(symbol: String): RealTimePrice {
        return quoteConsumer.getRealTimePrice(symbol)
    }
}