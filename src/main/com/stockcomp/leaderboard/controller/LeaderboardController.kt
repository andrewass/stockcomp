package com.stockcomp.leaderboard.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.dto.mapToLeaderboardEntryDto
import com.stockcomp.leaderboard.service.LeaderboardService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @GetMapping("/sorted-entries")
    fun getAllLeaderboardEntries(): ResponseEntity<List<LeaderboardEntryDto>> =
        leaderboardService.getSortedLeaderboardEntries()
            .map { mapToLeaderboardEntryDto(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/user-entry")
    fun getLeaderboardEntryForUser(@RequestParam ident: String): ResponseEntity<LeaderboardEntryDto> =
        leaderboardService.getLeaderboardEntryForUser(ident)
            ?.let { ResponseEntity.ok(mapToLeaderboardEntryDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}