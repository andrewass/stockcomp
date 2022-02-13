package com.stockcomp.service.symbol

import com.stockcomp.consumer.QuoteConsumer
import com.stockcomp.dto.stock.RealTimePriceDto
import org.springframework.stereotype.Service

@Service
class DefaultSymbolService(
    private val quoteConsumer: QuoteConsumer
) : SymbolService {

    override fun getRealTimePrice(symbol: String): RealTimePriceDto {
        return quoteConsumer.getRealTimePrice(symbol)
    }
}