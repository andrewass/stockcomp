package com.stockcomp.controller

import com.stockcomp.document.SymbolDocument
import com.stockcomp.response.HistoricPrice
import com.stockcomp.response.RealTimePrice
import com.stockcomp.response.SymbolSearch
import com.stockcomp.service.StockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class StockController(private val stockService: StockService) {

    @GetMapping("/search-symbol")
    fun searchSymbol(@RequestParam symbol: String): ResponseEntity<List<SymbolSearch>> {
        val results = stockService.searchSymbol(symbol)

        return ResponseEntity.ok(results)
    }

    @GetMapping("/historic-prices")
    fun getHistoricPrices(@RequestParam symbol: String): ResponseEntity<List<HistoricPrice>> {
        val result = stockService.getHistoricPriceList(symbol)

        return ResponseEntity.ok(result)
    }

    @GetMapping("/symbol-suggestions")
    fun getSymbolSuggestions(@RequestParam query: String): ResponseEntity<List<SymbolDocument>> {
        val results = stockService.getSymbolSuggestions(query)

        return ResponseEntity.ok(results)
    }

    @GetMapping("/real-time-price")
    fun getRealTimePrice(@RequestParam symbol: String): ResponseEntity<RealTimePrice> {
        val result = stockService.getRealTimePrice(symbol)

        return ResponseEntity.ok(result)
    }
}