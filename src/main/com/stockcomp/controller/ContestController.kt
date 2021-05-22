package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.controller.common.extractUsername
import com.stockcomp.controller.common.getJwtFromCookie
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
    private val investmentService: InvestmentService
) : CustomExceptionHandler() {

    @ApiOperation(value = "Return a list of upcoming contests")
    @GetMapping("/upcoming-contests")
    fun upcomingContests(httpServletRequest: HttpServletRequest): ResponseEntity<List<UpcomingContest>> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = jwt?.let { extractUsername(jwt) }
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

    @PostMapping("/buy-investment")
    @ApiOperation(value = "Place a buy order for a given participant")
    fun buyInvestment(
        httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentTransactionRequest
    ): ResponseEntity<HttpStatus> {
        val username = extractUsernameFromRequest(httpServletRequest)
        investmentService.placeBuyOrder(investmentRequest, username)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/sell-investment")
    @ApiOperation(value = "Place a sell order for a given participant")
    fun sellInvestment(
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
    ): ResponseEntity<InvestmentDto> {
        val username = extractUsernameFromRequest(httpServletRequest)
        val investments = investmentService.getInvestmentForSymbol(username, contestNumber, symbol)

        return ResponseEntity.ok(investments)
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
        val jwt = getJwtFromCookie(request)

        return extractUsername(jwt!!)
    }
}