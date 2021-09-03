package com.stockcomp.controller

import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.response.InvestmentDto
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
    @ApiOperation(value = "Get user investment for a given symbol")
    fun getInvestmentForSymbol(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int, @RequestParam symbol: String
    ): ResponseEntity<InvestmentDto> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.getInvestmentForSymbol(username, contestNumber, symbol)?.let {
            return ResponseEntity.ok(it)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/total-investments")
    @ApiOperation(value = "Get all user investments for given contest")
    fun getAllInvestmentsForContest(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<List<InvestmentDto>> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val investments = investmentService.getAllInvestmentsForContest(username, contestNumber)

        return ResponseEntity.ok(investments)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return defaultJwtService.extractUsername(jwt!!)
    }
}