package com.stockcomp.controller

import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.leaderboard.LeaderboardEntryDto
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/leaderboard")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
@Api(description = "Endpoints for leaderboard entries")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val defaultJwtService: DefaultJwtService
) {

    @GetMapping("/entries")
    @ApiOperation(value = "Get all leaderboard entries")
    fun getAllLeaderboardEntries(httpServletRequest: HttpServletRequest): ResponseEntity<List<LeaderboardEntryDto>> =
        leaderboardService.getSortedLeaderboardEntries()
            .let { ResponseEntity.ok(it) }


    @GetMapping("/user-entry")
    @ApiOperation(value = "Get leaderboard entry for given user")
    fun getLeaderboardEntryForUser(
        httpServletRequest: HttpServletRequest,
        @RequestParam(required = false) username: String?
    ): ResponseEntity<LeaderboardEntryDto> =
        (username ?: extractUsernameFromRequest(httpServletRequest))
            .let { leaderboardService.getLeaderboardEntryForUser(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity(HttpStatus.OK)


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}