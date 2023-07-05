package com.stockcomp.price.consumer

import com.stockcomp.contest.dto.RealTimePrice

interface QuoteConsumer {
    fun getRealTimePrice(symbol: String): RealTimePrice
}