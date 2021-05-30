package com.stockcomp.controller

import com.stockcomp.response.InvestmentOrderDto
import com.stockcomp.service.order.InvestmentOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/investment-order")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService
) {
    @GetMapping
    fun getAllCompletedOrdersForParticipant() : ResponseEntity<List<InvestmentOrderDto>>{
        val response = investmentOrderService.getAllCompletedOrdersForParticipant()

        return ResponseEntity.ok(response);
    }

    @GetMapping
    fun getAll() : ResponseEntity<List<InvestmentOrderDto>>{
        val response = investmentOrderService.getAllCompletedOrdersForSymbolForParticipant()

        return ResponseEntity.ok(response);
    }
}