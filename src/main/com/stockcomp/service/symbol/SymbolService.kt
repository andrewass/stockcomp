package com.stockcomp.service.symbol

import com.stockcomp.response.RealTimePrice

interface SymbolService {
    fun getRealTimePrice(symbol: String): RealTimePrice
}