package com.stockcomp.contest.service

import com.stockcomp.contest.dto.RealTimePriceDto

interface SymbolService {
    fun getRealTimePrice(symbol: String): RealTimePriceDto
}