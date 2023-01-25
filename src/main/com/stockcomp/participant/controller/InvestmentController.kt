package com.stockcomp.participant.controller

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.dto.InvestmentDto
import com.stockcomp.participant.dto.mapToInvestmentDto
import com.stockcomp.participant.service.InvestmentService
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val investmentService: InvestmentService,
    private val tokenService: TokenService
) {

    @GetMapping("/get-all")
    fun getAllFromContest(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam contestNumber: Int, @RequestParam ident: String
    ): ResponseEntity<List<InvestmentDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let {  investmentService.getAllInvestmentsForParticipant(it, contestNumber) }
            .map { mapToInvestmentDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/get-by-symbol")
    fun getAllFromContest(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: GetInvestmentBySymbolRequest
    ): ResponseEntity<InvestmentDto?> =
        tokenService.extractEmailFromToken(jwt)
            .let {  investmentService.getInvestmentForSymbol(request.contestNumber, it, request.symbol) }
            ?.let { ResponseEntity.ok(mapToInvestmentDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}