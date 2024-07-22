package com.stockcomp.participant.investmentorder

import com.stockcomp.token.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/investmentorder")
class InvestmentOrderController(
    private val investmentOrderService: InvestmentOrderService,
    private val tokenService: TokenService
) {

    @PostMapping("/post")
    fun placeInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let {
                investmentOrderService.placeInvestmentOrder(
                    contestNumber = request.contestNumber,
                    symbol = request.symbol,
                    email = it,
                    acceptedPrice = request.acceptedPrice,
                    expirationTime = request.expirationTime,
                    amount = request.amount,
                    currency = request.currency,
                    transactionType = request.transactionType
                )
            }
            .let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/delete")
    fun deleteInvestmentOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam orderId: Long,
        @RequestParam contestNumber: Int
    ): ResponseEntity<HttpStatus> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.deleteInvestmentOrder(it, orderId, contestNumber) }
            .let { ResponseEntity(HttpStatus.OK) }

    @GetMapping("/all-active")
    fun getActiveInvestmentOrders(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.getActiveOrders(contestNumber, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/all-completed")
    fun getCompletedInvestmentOrders(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.getCompletedOrders(contestNumber, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/symbol-active")
    fun getActiveInvestmentOrdersSymbol(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam contestNumber: Int,
        @RequestParam symbol: String
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.getActiveOrdersSymbol(symbol, contestNumber, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/symbol-completed")
    fun getCompletedInvestmentOrdersSymbol(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam contestNumber: Int,
        @RequestParam symbol: String
    ): ResponseEntity<List<InvestmentOrderDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentOrderService.getCompletedOrdersSymbol(symbol, contestNumber, it) }
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    data class PlaceInvestmentOrderRequest(
        val contestNumber: Int,
        val symbol: String,
        val amount: Int,
        val currency: String,
        val expirationTime: LocalDateTime,
        val acceptedPrice: Double,
        val transactionType: TransactionType
    )
}