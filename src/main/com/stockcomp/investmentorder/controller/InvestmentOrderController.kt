package com.stockcomp.investmentorder.controller

import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
import com.stockcomp.investmentorder.dto.InvestmentOrderDto
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.authentication.controller.getAccessTokenFromCookie
import com.stockcomp.authentication.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val jwtService: JwtService
) {

    @PostMapping("/place-order")
    fun placeInvestmentOrder(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(servletRequest)
            .let { investmentOrderService.placeInvestmentOrder(investmentOrderRequest, it) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/delete-order")
    fun deleteInvestmentOrder(
        servletRequest: HttpServletRequest,
        @RequestParam orderId: Long
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(servletRequest)
            .let { investmentOrderService.deleteInvestmentOrder(it, orderId) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/get-by-status")
    fun getInvestmentOrders(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        extractUsernameFromRequest(servletRequest)
            .let { investmentOrderService.getOrdersByStatus(it, investmentOrderRequest) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("get-by-status-symbol")
    fun getInvestmentOrdersSymbol(
        servletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: GetInvestmentOrderRequest,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        extractUsernameFromRequest(servletRequest)
            .let { investmentOrderService.getSymbolOrdersByStatus(it, investmentOrderRequest) }
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(servletRequest: HttpServletRequest): String =
        getAccessTokenFromCookie(servletRequest)
            .let { jwtService.extractUsername(it!!) }
}