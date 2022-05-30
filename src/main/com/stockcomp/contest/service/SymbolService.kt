package com.stockcomp.contest.service

import com.stockcomp.contest.dto.RealTimePrice

interface SymbolService {
    fun getRealTimePrice(symbol: String): RealTimePrice
}