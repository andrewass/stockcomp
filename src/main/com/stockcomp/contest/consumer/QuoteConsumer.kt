package com.stockcomp.contest.consumer

import com.stockcomp.contest.dto.RealTimePrice

interface QuoteConsumer {
    fun getRealTimePrice(symbol: String): RealTimePrice
}