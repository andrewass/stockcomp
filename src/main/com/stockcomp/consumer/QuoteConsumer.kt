package com.stockcomp.consumer

import com.stockcomp.dto.stock.RealTimePriceDto

interface QuoteConsumer {
    fun getRealTimePrice(symbol: String): RealTimePriceDto
}