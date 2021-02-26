package com.stockcomp.controller;

import com.stockcomp.response.RealTimePriceResponse;
import com.stockcomp.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/real-time-price")
    public ResponseEntity<RealTimePriceResponse> getRealTimePrice(@RequestParam("symbol") String symbol){
        var result = stockService.getRealTimePrice(symbol);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
