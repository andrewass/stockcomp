package com.stockcomp.investmentorder.controller

import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.InvestmentOrderDto
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.mapToInvestmentOrderDto
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.OrderProcessService
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val orderProcessService : OrderProcessService,
    private val tokenService: TokenService
) {

    @PostMapping("/place-order")
    fun placeInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody investmentOrderRequest: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        investmentOrderService.placeInvestmentOrder(investmentOrderRequest)
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/delete-order")
    fun deleteInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam orderId: Long,
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let {  investmentOrderService.deleteInvestmentOrder(it, orderId) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/get-by-status")
    fun getInvestmentOrders(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let {  investmentOrderService.getOrdersByStatus(request.contestNumber, request.statusList, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/get-by-status-symbol")
    fun getInvestmentOrdersSymbol(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let {  investmentOrderService.getSymbolOrdersByStatus(
                request.contestNumber, request.symbol!!,
                request.statusList, it
            ) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/process-orders")
    fun processInvestmentOrders() : Boolean {
        orderProcessService.processInvestmentOrders()
        return true
    }
}