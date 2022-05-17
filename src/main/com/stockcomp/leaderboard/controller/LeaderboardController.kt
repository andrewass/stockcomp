package com.stockcomp.leaderboard.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.authentication.service.DefaultJwtService
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

    @GetMapping("/sorted-entries")
    fun getAllLeaderboardEntries(servletRequest: HttpServletRequest): ResponseEntity<List<LeaderboardEntryDto>> =
        leaderboardService.getSortedLeaderboardEntries()
            .let { ResponseEntity.ok(it) }


    @GetMapping("/user-entry")
    fun getLeaderboardEntryForUser(
        servletRequest: HttpServletRequest,
        @RequestParam(required = false) username: String?
    ): ResponseEntity<LeaderboardEntryDto> =
        (username ?: extractUsernameFromRequest(servletRequest))
            .let { leaderboardService.getLeaderboardEntryForUser(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity(HttpStatus.OK)


    private fun extractUsernameFromRequest(servletRequest: HttpServletRequest): String =
        getAccessTokenFromCookie(servletRequest)
            .let { defaultJwtService.extractUsername(it!!) }
}