package com.stockcomp.investmentorder.controller

import com.stockcomp.investmentorder.dto.*
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.investmentorder.service.InvestmentOrderProcessService
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val orderProcessService: InvestmentOrderProcessService,
    private val tokenService: TokenService
) {

    @PostMapping("/place-order")
    fun placeInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody investmentOrderRequest: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.placeInvestmentOrder(investmentOrderRequest, it) }
            .let { ResponseEntity(HttpStatus.OK) }


    @DeleteMapping("/delete-order")
    fun deleteInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam orderId: Long,
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.deleteInvestmentOrder(it, orderId) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/get-all-by-status")
    fun getInvestmentOrders(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: GetAllInvestmentOrders,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.getAllOrdersByStatus(request.statusList, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/get-by-status-symbol")
    fun getInvestmentOrdersSymbol(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: GetInvestmentOrderBySymbolRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let {
                investmentOrderService.getSymbolOrdersByStatus(
                    request.contestNumber, request.symbol!!,
                    request.statusList, it
                )
            }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

}