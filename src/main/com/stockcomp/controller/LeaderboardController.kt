package com.stockcomp.controller

import com.stockcomp.controller.common.getAccessTokenFromCookie
import com.stockcomp.dto.leaderboard.LeaderboardEntryDto
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    fun getAllLeaderboardEntries(httpServletRequest: HttpServletRequest): ResponseEntity<List<LeaderboardEntryDto>> {
        val entries = leaderboardService.getSortedLeaderboardEntries()

        return ResponseEntity.ok(entries)
    }

    @GetMapping("/user-entry")
    @ApiOperation(value = "Get leaderboard entry for given user")
    fun getLeaderboardEntryForUser(httpServletRequest: HttpServletRequest): ResponseEntity<LeaderboardEntryDto> {
        val userName = extractUsernameFromRequest(httpServletRequest)
        val entry = leaderboardService.getLeaderboardEntryForUser(userName)

        return ResponseEntity.ok(entry)
    }

    private fun extractUsernameFromRequest(request: HttpServletRequest): String {
        val jwt = getAccessTokenFromCookie(request)

        return defaultJwtService.extractUsername(jwt!!)
    }
}