package com.stockcomp.participant.investmentorder

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/investmentorders")
class InvestmentOrderController(
    private val investmentOrderProcessingService: InvestmentOrderProcessingService
) {

    @PostMapping("/order")
    fun placeInvestmentOrder(
        @TokenData tokenClaims: TokenClaims,
        @RequestBody request: PlaceInvestmentOrderRequest
    ): ResponseEntity<HttpStatus> =
        investmentOrderProcessingService.placeInvestmentOrder(
            participantId = request.participantId,
            symbol = request.symbol,
            acceptedPrice = request.acceptedPrice,
            expirationTime = request.expirationTime,
            amount = request.amount,
            currency = request.currency,
            transactionType = request.transactionType
        ).let { ResponseEntity(HttpStatus.OK) }

    @DeleteMapping("/delete")
    fun deleteInvestmentOrder(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam orderId: Long,
        @RequestParam contestId: Long
    ): ResponseEntity<HttpStatus> =
        investmentOrderProcessingService.deleteInvestmentOrder(
            userId = tokenClaims.userId, orderId = orderId, contestId = contestId
        ).let { ResponseEntity(HttpStatus.OK) }

    @GetMapping("/all-active")
    fun getActiveInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam contestId: Long
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderProcessingService.getActiveOrders(contestId, tokenClaims.userId)
            .map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/all-completed")
    fun getCompletedInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam contestId: Long
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderProcessingService.getCompletedOrders(
            contestId = contestId, userId = tokenClaims.userId
        ).map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/symbol-active")
    fun getActiveInvestmentOrdersSymbol(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam contestId: Long,
        @RequestParam symbol: String
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderProcessingService.getActiveOrdersSymbol(
            symbol = symbol, contestId = contestId, userId = tokenClaims.userId
        ).map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/symbol-completed")
    fun getCompletedInvestmentOrdersSymbol(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam contestId: Long,
        @RequestParam symbol: String
    ): ResponseEntity<List<InvestmentOrderDto>> =
        investmentOrderProcessingService.getCompletedOrdersSymbol(
            symbol = symbol, contestId = contestId, userId = tokenClaims.userId
        ).map { mapToInvestmentOrderDto(it) }
            .let { ResponseEntity.ok(it) }

    data class PlaceInvestmentOrderRequest(
        val participantId: Long,
        val symbol: String,
        val amount: Int,
        val currency: String,
        val expirationTime: LocalDateTime,
        val acceptedPrice: Double,
        val transactionType: TransactionType
    )
}
