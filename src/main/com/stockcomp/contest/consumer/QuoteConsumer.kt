package com.stockcomp.contest.consumer

import com.stockcomp.contest.dto.RealTimePriceDto

interface QuoteConsumer {
    fun getRealTimePrice(symbol: String): RealTimePriceDto
}