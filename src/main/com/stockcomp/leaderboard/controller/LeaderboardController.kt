package com.stockcomp.leaderboard.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.dto.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.dto.mapToLeaderboardEntryDto
import com.stockcomp.leaderboard.dto.mapToLeaderboardEntryPageDto
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val tokenService: TokenService
) {

    @GetMapping("/sorted-entries")
    fun getAllLeaderboardEntries(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<LeaderboardEntryPageDto> =
        leaderboardService.getSortedLeaderboardEntries(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToLeaderboardEntryPageDto(it, pageNumber)) }


    @GetMapping("/user-entry")
    fun getLeaderboardEntryForUser(
        @RequestParam username: String?,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<LeaderboardEntryDto> =
        tokenService.extractEmailFromToken(jwt)
            .let { leaderboardService.getLeaderboardEntryForEmail(it) }
            ?.let { ResponseEntity.ok(mapToLeaderboardEntryDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}