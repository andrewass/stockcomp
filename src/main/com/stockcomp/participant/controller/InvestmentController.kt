package com.stockcomp.participant.controller

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.dto.InvestmentDto
import com.stockcomp.participant.service.InvestmentService
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val investmentService: InvestmentService,
    private val jwtService: JwtService
) {

    @GetMapping("/get-all")
    fun getAllFromContest(
        @RequestParam contestNumber: Int, request: HttpServletRequest
    ) : ResponseEntity<List<InvestmentDto>> =
        extractUsernameFromRequest(request)
            .let { ResponseEntity.ok(investmentService.getAllInvestmentsForParticipant(it, contestNumber)) }


    @PostMapping("/get-by-symbol")
    fun getAllFromContest(
        @RequestBody getInvestmentBySymbolRequest: GetInvestmentBySymbolRequest, servletRequest: HttpServletRequest
    ) : ResponseEntity<InvestmentDto?> =
        extractUsernameFromRequest(servletRequest)
            .let { ResponseEntity.ok(investmentService.getInvestmentForSymbol(it, getInvestmentBySymbolRequest)) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { jwtService.extractUsername(it!!) }
}