package com.stockcomp.leaderboard.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.service.security.DefaultJwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val defaultJwtService: DefaultJwtService
) {

    @GetMapping("/entries")
    fun getAllLeaderboardEntries(httpServletRequest: HttpServletRequest): ResponseEntity<List<LeaderboardEntryDto>> =
        leaderboardService.getSortedLeaderboardEntries()
            .let { ResponseEntity.ok(it) }


    @GetMapping("/user-entry")
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