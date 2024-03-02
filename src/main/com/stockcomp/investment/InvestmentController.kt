package com.stockcomp.investment

import com.stockcomp.investment.dto.InvestmentDto
import com.stockcomp.participant.dto.mapToInvestmentDto
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val investmentService: InvestmentService,
    private val tokenService: TokenService
) {

    @GetMapping("/all")
    fun getAllFromContest(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<InvestmentDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentService.getAllInvestmentsForParticipant(it) }
            .map { mapToInvestmentDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping
    fun getInvestmentBySymbolAndContest(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam symbol: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<InvestmentDto?> =
        tokenService.extractEmailFromToken(jwt)
            .let { investmentService.getInvestmentForSymbol(contestNumber, it, symbol) }
            ?.let { ResponseEntity.ok(mapToInvestmentDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}