package com.stockcomp.symbol

interface SymbolServiceExternal {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto
}
