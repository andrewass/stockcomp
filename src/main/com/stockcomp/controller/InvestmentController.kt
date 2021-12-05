package com.stockcomp.controller

import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.InvestmentDto
import com.stockcomp.service.investment.InvestmentService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val investmentService: InvestmentService,
    private val defaultJwtService: DefaultJwtService
) {

    @GetMapping("/symbol-investment")
    @ApiOperation(value = "Get investment for a given symbol for a participant")
    fun getInvestmentForSymbol(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int, @RequestParam symbol: String
    ): ResponseEntity<InvestmentDto> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentService.getInvestmentForSymbol(it, contestNumber, symbol) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity(HttpStatus.OK)


    @GetMapping("/total-investments")
    @ApiOperation(value = "Get all investments for a participant")
    fun getAllInvestmentsForContest(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentDto>> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentService.getAllInvestmentsForContest(it, contestNumber) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/total-investment-value")
    @ApiOperation(value = "Get total investment value for participant")
    fun getTotalInvestmentValue(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentService.getTotalValue(it, contestNumber) }
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}