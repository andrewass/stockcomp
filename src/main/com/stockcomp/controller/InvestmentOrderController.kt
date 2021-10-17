package com.stockcomp.controller

import com.stockcomp.service.security.DefaultJwtService
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.response.InvestmentOrderDto
import com.stockcomp.service.order.InvestmentOrderService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment-order")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
@Api(description = "Endpoints for investment order operations")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val defaultJwtService: DefaultJwtService
) {

    @PostMapping("/place-buy-order")
    @ApiOperation(value = "Place a buy order for a given participant")
    fun placeBuyOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentOrderRequest
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentOrderService.placeBuyOrder(investmentRequest, username)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/place-sell-order")
    @ApiOperation(value = "Place a sell order for a given participant")
    fun placeSellOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentOrderRequest
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentOrderService.placeSellOrder(investmentRequest, username)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/delete-active-order")
    @ApiOperation(value = "Delete an active investment order")
    fun deleteActiveOrder(
        httpServletRequest: HttpServletRequest,
        @RequestParam orderId: Long
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentOrderService.deleteActiveInvestmentOrder(username, orderId)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/active-orders-participant")
    @ApiOperation(value = "Get all active investment orders for a participant")
    fun getAllActiveOrdersForParticiapant(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val response = investmentOrderService.getAllActiveOrdersForParticipant(username, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/active-orders-symbol-participant")
    @ApiOperation(value = "Get all active investment orders for a given symbol and participant")
    fun getAllActiveOrdersForSymbolForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam symbol: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getAccessTokenFromCookie(httpServletRequest)
        val username = jwt?.let { defaultJwtService.extractUsername(jwt) }
        val response = investmentOrderService
            .getAllActiveOrdersForSymbolForParticipant(username!!, symbol, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/completed-orders-participant")
    @ApiOperation(value = "Get all completed investment orders for a participant")
    fun getAllCompletedOrdersForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getAccessTokenFromCookie(httpServletRequest)
        val username = jwt?.let { defaultJwtService.extractUsername(jwt) }
        val response = investmentOrderService.getAllCompletedOrdersForParticipant(username!!, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/completed-orders-symbol-participant")
    @ApiOperation(value = "Get all completed investment orders for a given symbol and participant")
    fun getAllCompletedOrdersForSymbolForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam symbol: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getAccessTokenFromCookie(httpServletRequest)
        val username = jwt?.let { defaultJwtService.extractUsername(jwt) }
        val response = investmentOrderService
            .getAllCompletedOrdersForSymbolForParticipant(username!!, symbol, contestNumber)

        return ResponseEntity.ok(response)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return defaultJwtService.extractUsername(jwt!!)
    }

}