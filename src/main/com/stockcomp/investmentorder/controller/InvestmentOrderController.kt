package com.stockcomp.investmentorder.controller

import com.stockcomp.investmentorder.dto.InvestmentOrderRequest
import com.stockcomp.investmentorder.service.InvestmentOrderService
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val jwtService: JwtService

) {

    @PostMapping("/place-order")
    fun placeInvestmentOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentOrderRequest: InvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentOrderService.placeInvestmentOrder(investmentOrderRequest, it) }
            .let { ResponseEntity(HttpStatus.OK) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { jwtService.extractUsername(it!!) }
}