package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.ParticipantDto
import com.stockcomp.dto.UpcomingContestParticipantDto
import com.stockcomp.service.contest.ContestService
import com.stockcomp.service.investment.InvestmentService
import com.stockcomp.service.security.JwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
@Api(description = "Endpoints for contest related operations")
class ContestController(
    private val contestService: ContestService,
    private val investmentService: InvestmentService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {

    @GetMapping("/upcoming-contests")
    @ApiOperation(value = "Return a list of upcoming contests")
    fun upcomingContests(httpServletRequest: HttpServletRequest): ResponseEntity<List<UpcomingContestParticipantDto>> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getUpcomingContestsParticipant(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/all-contests")
    @ApiOperation(value = "Return a list of all contests")
    fun allContests(httpServletRequest: HttpServletRequest): ResponseEntity<List<ContestDto>> =
        contestService.getAllContests()
            .let { ResponseEntity.ok(it) }


    @PostMapping("/sign-up")
    @ApiOperation(value = "Sing up user for a given contest")
    fun signUpForContest(
        httpServletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<HttpStatus> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.signUpUser(it, contestNumber) }
            .let { ResponseEntity(HttpStatus.OK) }


    @GetMapping("/remaining-funds")
    @ApiOperation(value = "Get remaining funds for a participant")
    fun getRemainingFunds(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> =
        extractUsernameFromRequest(httpServletRequest)
            .let { investmentService.getRemainingFunds(it, contestNumber) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/participants-by-rank")
    @ApiOperation(value = "Get contest participants sorted by ranking")
    fun getParticipantsByRanking(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<List<ParticipantDto>> =
        contestService.getParticipantsByRank(contestNumber)
            .let { ResponseEntity.ok(it) }


    @GetMapping("/participant")
    @ApiOperation("Get participant of a given contest")
    fun getParticipant(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<ParticipantDto> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getParticipant(contestNumber, it) }
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}