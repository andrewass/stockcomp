package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.controller.common.getJwtFromCookie
import com.stockcomp.entity.contest.Transaction
import com.stockcomp.request.InvestmentTransactionRequest
import com.stockcomp.response.UpcomingContest
import com.stockcomp.service.ContestService
import com.stockcomp.service.InvestmentService
import com.stockcomp.util.extractUsername
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
    fun upcomingContests(request: HttpServletRequest): ResponseEntity<List<UpcomingContest>> {
        val jwt = getJwtFromCookie(request)
        val username = jwt?.let { extractUsername(jwt) }
        val contests = contestService.getUpcomingContests(username)

        return ResponseEntity.ok(contests)
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sing up user for a given contest")
    fun signUpForContest(request: HttpServletRequest, @RequestParam contestNumber: Int): ResponseEntity<HttpStatus> {
        val jwt = getJwtFromCookie(request)
        val username = extractUsername(jwt!!)
        contestService.signUpUser(username, contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/user-participating")
    @ApiOperation(value = "Verify if user is participating in a given contest")
    fun isParticipating(request: HttpServletRequest, @RequestParam contestNumber: Int): ResponseEntity<Boolean> {
        val jwt = getJwtFromCookie(request)
        val username = extractUsername(jwt!!)
        val isParticipating = contestService.userIsParticipating(username, contestNumber)

        return ResponseEntity.ok(isParticipating)
    }

    @PostMapping("/buy-investment")
    @ApiOperation(value = "Buy investment for a given participant")
    fun buyInvestment(httpServletRequest: HttpServletRequest,
                      @RequestBody investmentRequest: InvestmentTransactionRequest): ResponseEntity<Transaction> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = extractUsername(jwt!!)
        val transaction = investmentService.buyInvestment(investmentRequest, username)

        return ResponseEntity.ok(transaction)
    }

    @PostMapping("/sell-investment")
    @ApiOperation(value = "Sell investment for a given participant")
    fun sellInvestment(httpServletRequest: HttpServletRequest,
        @RequestBody investmentRequest: InvestmentTransactionRequest): ResponseEntity<Transaction> {
        val jwt = getJwtFromCookie(httpServletRequest)
        val username = extractUsername(jwt!!)
        val transaction = investmentService.sellInvestment(investmentRequest, username)

        return ResponseEntity.ok(transaction)
    }
}