package com.stockcomp.price.consumer

import com.stockcomp.contest.domain.CurrentPriceSymbol

interface QuoteConsumer {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbol
}