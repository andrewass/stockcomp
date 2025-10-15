package com.stockcomp.leaderboard.leaderboard

import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val leaderboardQueryService: LeaderboardQueryService
) {

    @GetMapping("/sorted")
    fun getAllLeaderboardEntries(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<LeaderboardEntryPageDto> =
        leaderboardQueryService.getSortedLeaderboardEntries(pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToLeaderboardEntryPageDto(it)) }

    @GetMapping("/user/{userId}")
    fun getLeaderboardEntryForUser(
        @PathVariable userId: Long
    ): ResponseEntity<LeaderboardEntryDto> =
        leaderboardQueryService.getLeaderboardEntryForUser(userId)
            .let { ResponseEntity.ok(mapToLeaderboardEntryDto(it)) }

    @PostMapping("/update")
    fun updateLeaderboard(
        @RequestParam contestId: Long
    ) {
        leaderboardService.updateLeaderboard(contestId)
    }
}