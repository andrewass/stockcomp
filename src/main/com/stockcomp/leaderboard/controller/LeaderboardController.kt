package com.stockcomp.leaderboard.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.dto.mapToLeaderboardEntryDto
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val tokenService: TokenService
) {

    @GetMapping("/sorted-entries")
    fun getAllLeaderboardEntries(): ResponseEntity<List<LeaderboardEntryDto>> =
        leaderboardService.getSortedLeaderboardEntries()
            .map { mapToLeaderboardEntryDto(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/user-entry")
    fun getLeaderboardEntryForUser(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<LeaderboardEntryDto> =
        tokenService.extractEmailFromToken(jwt)
            .let { leaderboardService.getLeaderboardEntryForUser(it) }
            ?.let { ResponseEntity.ok(mapToLeaderboardEntryDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}