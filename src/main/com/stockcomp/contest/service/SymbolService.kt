package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CurrentPriceSymbol

interface SymbolService {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbol
}