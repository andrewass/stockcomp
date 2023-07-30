package com.stockcomp.price.consumer

import com.stockcomp.contest.dto.CurrentPriceSymbol

interface QuoteConsumer {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbol
}