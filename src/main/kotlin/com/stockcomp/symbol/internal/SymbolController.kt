package com.stockcomp.symbol.internal

import com.stockcomp.symbol.CurrentPriceSymbolDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/symbols")
class SymbolController(
    private val symbolService: SymbolServiceInternal,
) {
    @GetMapping("/price/trending")
    fun getCurrentPriceTrendingSymbols(): ResponseEntity<TrendingSymbolsResponse> =
        ResponseEntity.ok(TrendingSymbolsResponse(symbolService.getCurrentPriceTrendingSymbols()))

    data class TrendingSymbolsResponse(
        val symbols: List<CurrentPriceSymbolDto>,
    )
}
