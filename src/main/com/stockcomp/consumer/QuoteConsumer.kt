package com.stockcomp.consumer

import com.stockcomp.response.RealTimePrice

interface QuoteConsumer {
    fun getRealTimePrice(symbol : String) : RealTimePrice
}