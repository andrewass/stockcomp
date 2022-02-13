package com.stockcomp.controller

import com.stockcomp.controller.common.CustomExceptionHandler
import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.contest.ContestParticipantDto
import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.service.contest.ContestService
import com.stockcomp.service.participant.ParticipantService
import com.stockcomp.service.security.JwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
@Api(description = "Endpoints for contest related operations")
class ContestController(
    private val contestService: ContestService,
    private val participantService: ParticipantService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {

    @GetMapping("/upcoming-contests")
    @ApiOperation(value = "Return a list of upcoming contests with participant status")
    fun upcomingContestsParticipant(httpServletRequest: HttpServletRequest): ResponseEntity<List<ContestParticipantDto>> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getUpcomingContestsParticipant(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/all-contests")
    @ApiOperation(value = "Return a list of all contests")
    fun allContests(httpServletRequest: HttpServletRequest): ResponseEntity<List<ContestDto>> =
        contestService.getAllContests()
            .let { ResponseEntity.ok(it) }


    @PostMapping("/contests-by-status")
    @ApiOperation(value = "Return a list of contest with participant status for a given list of contest status")
    fun contestsByStatus(
        httpServletRequest: HttpServletRequest, @RequestBody statusList: List<String>
    ): ResponseEntity<List<ContestParticipantDto>> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getContestParticipantsByStatus(statusList, it) }
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
            .let { participantService.getRemainingFunds(it, contestNumber) }
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


    @GetMapping("/participant-history")
    @ApiOperation("Get participant history for a given user")
    fun getParticipantHistory(
        httpServletRequest: HttpServletRequest, @RequestParam username: String
    ): ResponseEntity<List<ParticipantDto>> =
        contestService.getParticipantHistory(username)
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}