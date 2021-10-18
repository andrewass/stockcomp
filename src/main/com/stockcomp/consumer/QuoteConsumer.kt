package com.stockcomp.consumer

import com.stockcomp.dto.RealTimePrice

interface QuoteConsumer {
    fun getRealTimePrice(symbol: String): RealTimePrice
}