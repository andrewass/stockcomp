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
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class InvestmentController(
    private val investmentService: InvestmentService,
    private val defaultJwtService: DefaultJwtService
) {

    @GetMapping("/symbol-investment")
    @ApiOperation(value = "Get investment for a given symbol for a participant")
    fun getInvestmentForSymbol(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int, @RequestParam symbol: String
    ): ResponseEntity<InvestmentDto> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.getInvestmentForSymbol(username, contestNumber, symbol)?.let {
            return ResponseEntity.ok(it)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/total-investments")
    @ApiOperation(value = "Get all investments for a participant")
    fun getAllInvestmentsForContest(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentDto>> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val investments = investmentService.getAllInvestmentsForContest(username, contestNumber)

        return ResponseEntity.ok(investments)
    }

    @GetMapping("/total-investment-value")
    @ApiOperation(value = "Get total investment value for participant")
    fun getTotalInvestmentValue(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val totalInvestmentReturns = investmentService.getTotalValue(username, contestNumber)

        return ResponseEntity.ok(totalInvestmentReturns)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return defaultJwtService.extractUsername(jwt!!)
    }
}