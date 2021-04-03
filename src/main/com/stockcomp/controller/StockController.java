package com.stockcomp.controller;

import com.stockcomp.response.RealTimePriceResponse;
import com.stockcomp.response.SymbolSearchResponse;
import com.stockcomp.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search-symbol")
    public ResponseEntity<List<SymbolSearchResponse>> searchSymbol(@RequestParam String symbol){
        var result = stockService.searchSymbol(symbol);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/real-time-price")
    public ResponseEntity<RealTimePriceResponse> getRealTimePrice(@RequestParam String symbol){
        var result = stockService.getRealTimePrice(symbol);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
