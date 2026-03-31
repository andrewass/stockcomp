package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.participant.InvestmentOrderDto
import com.stockcomp.participant.PlaceInvestmentOrderRequest
import com.stockcomp.participant.mapToInvestmentOrderDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/participants/investmentorders")
class InvestmentOrderController(
    private val investmentOrderProcessingService: InvestmentOrderProcessingService,
) {
    @PostMapping("/order")
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
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/delete")
    fun deleteInvestmentOrder(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive orderId: Long,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<Void> {
        investmentOrderProcessingService.deleteInvestmentOrder(
            userId = tokenClaims.userId,
            orderId = orderId,
            contestId = contestId,
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/all-active")
    fun getActiveInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            investmentOrderProcessingService.getActiveOrders(contestId, tokenClaims.userId).map { mapToInvestmentOrderDto(it) },
        )

    @GetMapping("/all-completed")
    fun getCompletedInvestmentOrders(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            investmentOrderProcessingService
                .getCompletedOrders(contestId = contestId, userId = tokenClaims.userId)
                .map { mapToInvestmentOrderDto(it) },
        )

    @GetMapping("/symbol-active")
    fun getActiveInvestmentOrdersSymbol(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
        @RequestParam @NotBlank symbol: String,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            investmentOrderProcessingService
                .getActiveOrdersSymbol(
                    symbol = symbol,
                    contestId = contestId,
                    userId = tokenClaims.userId,
                ).map { mapToInvestmentOrderDto(it) },
        )

    @GetMapping("/symbol-completed")
    fun getCompletedInvestmentOrdersSymbol(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
        @RequestParam @NotBlank symbol: String,
    ): ResponseEntity<List<InvestmentOrderDto>> =
        ResponseEntity.ok(
            investmentOrderProcessingService
                .getCompletedOrdersSymbol(
                    symbol = symbol,
                    contestId = contestId,
                    userId = tokenClaims.userId,
                ).map { mapToInvestmentOrderDto(it) },
        )
}
