package com.stockcomp.producer.rest

import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.service.order.InvestmentOrderService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment-order")
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
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentOrderService.placeBuyOrder(investmentRequest, it) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/place-sell-order")
    @ApiOperation(value = "Place a sell order for a given participant")
    fun placeSellOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentOrderService.placeSellOrder(investmentRequest, it) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/delete-active-order")
    @ApiOperation(value = "Delete an active investment order")
    fun deleteActiveOrder(
        httpServletRequest: HttpServletRequest,
        @RequestParam orderId: Long
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentOrderService.deleteActiveInvestmentOrder(it, orderId) }
            .let { ResponseEntity(HttpStatus.OK) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}