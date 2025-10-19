package com.stockcomp.participant.internal.investment

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/participants/investments")
class InvestmentController(
    private val investmentService: InvestmentProcessingService
) {

    @GetMapping("/all")
    fun getAllFromContest(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam contestId: Long
    ): ResponseEntity<List<InvestmentDto>> =
        investmentService.getInvestmentsForParticipant(
            contestId = contestId, userId = tokenClaims.userId
        )
            .map { mapToInvestmentDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping
    fun getInvestmentBySymbolAndContest(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam symbol: String,
        @RequestParam contestId: Long
    ): ResponseEntity<InvestmentDto?> =
        investmentService.getInvestmentForSymbol(
            contestId = contestId, symbol = symbol, userId = tokenClaims.userId
        )?.let { ResponseEntity.ok(mapToInvestmentDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}