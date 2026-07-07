package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.participant.InvestmentOrderDto
import com.stockcomp.participant.PlaceInvestmentOrderRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/participants/investment-orders")
class InvestmentOrderController(
    private val investmentOrderProcessingService: InvestmentOrderProcessingService,
) {
    @PostMapping
    fun placeInvestmentOrder(
        @TokenData tokenClaims: TokenClaims,
        @Valid @RequestBody request: PlaceInvestmentOrderRequest,
    ): ResponseEntity<Void> {
        investmentOrderProcessingService.placeInvestmentOrder(
            userId = tokenClaims.userId,
            participantId = request.participantId,
            symbol = request.symbol,
            acceptedPrice = request.acceptedPrice,
            expirationTime = request.expirationTime,
            amount = request.amount,
            currency = request.currency,
            transactionType = request.transactionType,
        )
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping("/{orderId}")
    fun deleteInvestmentOrder(
        @TokenData tokenClaims: TokenClaims,
        @PathVariable @Positive orderId: Long,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<Void> {
        investmentOrderProcessingService.deleteInvestmentOrder(
            userId = tokenClaims.userId,
            orderId = orderId,
            contestId = contestId,
        )
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/active")
    fun getActiveInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
        @RequestParam(required = false) symbol: String?,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            if (symbol == null) {
                investmentOrderProcessingService.getActiveOrders(contestId, tokenClaims.userId)
            } else {
                investmentOrderProcessingService.getActiveOrdersSymbol(
                    symbol = symbol,
                    contestId = contestId,
                    userId = tokenClaims.userId,
                )
            }.map { mapToInvestmentOrderDto(it) },
        )

    @GetMapping("/completed")
    fun getCompletedInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
        @RequestParam(required = false) symbol: String?,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            if (symbol == null) {
                investmentOrderProcessingService.getCompletedOrders(contestId = contestId, userId = tokenClaims.userId)
            } else {
                investmentOrderProcessingService.getCompletedOrdersSymbol(
                    symbol = symbol,
                    contestId = contestId,
                    userId = tokenClaims.userId,
                )
            }.map { mapToInvestmentOrderDto(it) },
        )
}
