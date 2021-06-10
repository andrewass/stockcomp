package com.stockcomp.controller

import com.stockcomp.controller.common.JwtUtil
import com.stockcomp.controller.common.getJwtFromCookie
import com.stockcomp.response.InvestmentOrderDto
import com.stockcomp.service.order.InvestmentOrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment-order")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/active-orders-participant")
    fun getAllActiveOrdersForParticiapant(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { jwtUtil.extractUsername(jwt) }
        val response = investmentOrderService.getAllActiveOrdersForParticipant(username!!, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/active-orders-symbol-participant")
    fun getAllActiveOrdersForSymbolForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam symbol: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { jwtUtil.extractUsername(jwt) }
        val response = investmentOrderService
            .getAllActiveOrdersForSymbolForParticipant(username!!, symbol, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/completed-orders-participant")
    fun getAllCompletedOrdersForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { jwtUtil.extractUsername(jwt) }
        val response = investmentOrderService.getAllCompletedOrdersForParticipant(username!!, contestNumber)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/completed-orders-symbol-participant")
    fun getAllCompletedOrdersForSymbolForParticipant(
        httpServletRequest: HttpServletRequest,
        @RequestParam symbol: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { jwtUtil.extractUsername(jwt) }
        val response = investmentOrderService
            .getAllCompletedOrdersForSymbolForParticipant(username!!, symbol, contestNumber)

        return ResponseEntity.ok(response)
    }

    @PostMapping("/delete-active-order")
    fun deleteActiveOrder(
        httpServletRequest: HttpServletRequest,
        @RequestParam orderId: Long
    ): ResponseEntity<HttpStatus> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { jwtUtil.extractUsername(jwt) }
        investmentOrderService.deleteActiveInvestmentOrder(username!!, orderId)

        return ResponseEntity(HttpStatus.OK)
    }
}