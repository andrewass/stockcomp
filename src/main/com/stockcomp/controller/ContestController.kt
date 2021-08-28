package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.service.security.DefaultJwtService
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.InvestmentDto
import com.stockcomp.response.UpcomingContest
import com.stockcomp.service.ContestService
import com.stockcomp.service.investment.InvestmentService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class ContestController(
    private val contestService: ContestService,
    private val investmentService: InvestmentService,
    private val defaultJwtService: DefaultJwtService
) : CustomExceptionHandler() {

    @GetMapping("/upcoming-contests")
    @ApiOperation(value = "Return a list of upcoming contests")
    fun upcomingContests(httpServletRequest: HttpServletRequest): ResponseEntity<List<UpcomingContest>> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val contests = contestService.getUpcomingContests(username)

        return ResponseEntity.ok(contests)
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sing up user for a given contest")
    fun signUpForContest(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        contestService.signUpUser(username, contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/user-participating")
    @ApiOperation(value = "Verify if user is participating in a given contest")
    fun isParticipating(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<Boolean> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val isParticipating = contestService.userIsParticipating(username, contestNumber)

        return ResponseEntity.ok(isParticipating)
    }

    @PostMapping("/place-buy-order")
    @ApiOperation(value = "Place a buy order for a given participant")
    fun placeBuyOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentTransactionRequest
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.placeBuyOrder(investmentRequest, username)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/place-sell-order")
    @ApiOperation(value = "Place a sell order for a given participant")
    fun placeSellOrder(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentTransactionRequest
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.placeSellOrder(investmentRequest, username)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/symbol-investment")
    @ApiOperation(value = "Get investments for a given symbol")
    fun getInvestmentForSymbol(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int, @RequestParam symbol: String
    ): ResponseEntity<InvestmentDto?> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.getInvestmentForSymbol(username, contestNumber, symbol)?.let {
            return ResponseEntity.ok(it)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/total-investment-returns")
    @ApiOperation(value = "Get total investment returns for participant")
    fun getTotalInvestmentReturns(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val totalInvestmentReturns = investmentService.getTotalInvestmentReturns(username, contestNumber)

        return ResponseEntity.ok(totalInvestmentReturns)
    }

    @GetMapping("/remaining-funds")
    @ApiOperation(value = "Get participants remaining funds")
    fun getRemainingFunds(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val remainingFunds = investmentService.getRemainingFunds(username, contestNumber)

        return ResponseEntity.ok(remainingFunds)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return defaultJwtService.extractUsername(jwt!!)
    }
}