package com.stockcomp.investmentorder.controller

import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.InvestmentOrderDto
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.mapToInvestmentOrderDto
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.OrderProcessService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val orderProcessService : OrderProcessService
) {

    @PostMapping("/place-order")
    fun placeInvestmentOrder(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        investmentOrderService.placeInvestmentOrder(investmentOrderRequest)
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/delete-order")
    fun deleteInvestmentOrder(
        servletRequest: HttpServletRequest,
        @RequestParam orderId: Long, @RequestParam ident: String
    ): ResponseEntity<HttpStatus> =
        investmentOrderService.deleteInvestmentOrder(ident, orderId)
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/get-by-status")
    fun getInvestmentOrders(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderService.getOrdersByStatus(investmentOrderRequest)
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/get-by-status-symbol")
    fun getInvestmentOrdersSymbol(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderService.getSymbolOrdersByStatus(investmentOrderRequest)
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping("/process-orders")
    fun processInvestmentOrders() : Boolean {
        orderProcessService.processInvestmentOrders()
        return true
    }
}