package com.stockcomp.controller;

import com.stockcomp.document.SymbolDocument;
import com.stockcomp.response.HistoricPriceResponse;
import com.stockcomp.response.RealTimePriceResponse;
import com.stockcomp.response.SymbolSearchResponse;
import com.stockcomp.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:8000", allowCredentials = "true")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search-symbol")
    public ResponseEntity<List<SymbolSearchResponse>> searchSymbol(@RequestParam String symbol){
        var results = stockService.searchSymbol(symbol);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/historic-prices")
    public ResponseEntity<List<HistoricPriceResponse>> getHistoricPrices(@RequestParam String symbol){
        var result = stockService.getHistoricPriceList(symbol);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/symbol-suggestions")
    public ResponseEntity<List<SymbolDocument>> getSymbolSuggestions(@RequestParam String query){
        var results = stockService.getSymbolSuggestions(query);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/real-time-price")
    public ResponseEntity<RealTimePriceResponse> getRealTimePrice(@RequestParam String symbol){
        var result = stockService.getRealTimePrice(symbol);

        return ResponseEntity.ok(result);
    }
}
