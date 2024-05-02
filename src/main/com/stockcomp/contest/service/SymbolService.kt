package com.stockcomp.contest.service

import com.stockcomp.contest.domain.CurrentPriceSymbol

interface SymbolService {
    fun getCurrentPrice(symbol: String): CurrentPriceSymbol
}