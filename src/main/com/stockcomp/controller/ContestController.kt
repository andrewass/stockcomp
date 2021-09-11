package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.response.UpcomingContest
import com.stockcomp.service.contest.DefaultContestService
import com.stockcomp.service.investment.InvestmentService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class ContestController(
    private val contestService: DefaultContestService,
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

    @GetMapping("/remaining-funds")
    @ApiOperation(value = "Get remaining funds for a participant")
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