package com.stockcomp.service.symbol

import com.stockcomp.dto.RealTimePrice

interface SymbolService {
    fun getRealTimePrice(symbol: String): RealTimePrice
}