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
class ContestController(
    private val contestService: ContestService,
    private val investmentService: InvestmentService
) :
    CustomExceptionHandler() {

    @ApiOperation(value = "Return a list of upcoming contests")
    @GetMapping("/upcoming-contests")
    fun upcomingContests(): ResponseEntity<List<UpcomingContest>> {
        val contests = contestService.getUpcomingContests()

        return ResponseEntity.ok(contests)
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sing up user for a given contest")
    fun signUpForContest(
        @RequestParam username: String,
        @RequestParam contestNumber: Int
    ): ResponseEntity<HttpStatus> {
        contestService.signUpUser(username, contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/user-participating")
    @ApiOperation(value = "Verify if user is participating in a given contest")
    fun isParticipating(request: HttpServletRequest, @RequestParam contestNumber: Int): ResponseEntity<Boolean> {
        val jwt = getJwtFromCookie(request)
        val username = extractUsername(jwt)
        val isParticipating = contestService.userIsParticipating(username, contestNumber)

        return ResponseEntity.ok(isParticipating)
    }

    @PostMapping("/buy-investment")
    @ApiOperation(value = "Buy investment for a given participant")
    fun buyInvestment(@RequestBody request: InvestmentTransactionRequest): ResponseEntity<Transaction> {
        val transaction = investmentService.buyInvestment(request)

        return ResponseEntity.ok(transaction)
    }

    @PostMapping("/sell-investment")
    @ApiOperation(value = "Sell investment for a given participant")
    fun sellInvestment(@RequestBody request: InvestmentTransactionRequest): ResponseEntity<Transaction> {
        val transaction = investmentService.sellInvestment(request)

        return ResponseEntity.ok(transaction)
    }
}