package com.stockcomp.service.symbol

import com.stockcomp.dto.stock.RealTimePriceDto

interface SymbolService {
    fun getRealTimePrice(symbol: String): RealTimePriceDto
}