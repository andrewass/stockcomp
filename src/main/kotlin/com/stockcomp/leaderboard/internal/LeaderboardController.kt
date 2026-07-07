package com.stockcomp.leaderboard.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val leaderboardQueryService: LeaderboardQueryService,
) {
    @GetMapping
    fun getAllLeaderboardEntries(
        @RequestParam @PositiveOrZero pageNumber: Int,
        @RequestParam @Positive pageSize: Int,
    ): ResponseEntity<LeaderboardEntryPageDto> =
        ResponseEntity.ok(leaderboardQueryService.getSortedLeaderboardEntryPage(pageNumber, pageSize))

    @GetMapping("/me")
    fun getLeaderboardEntryForUser(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<LeaderboardEntryDto> = ResponseEntity.ok(leaderboardQueryService.getLeaderboardEntryDtoForUser(tokenClaims.userId))

    @PostMapping("/recalculations")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateLeaderboard(
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<Void> {
        leaderboardService.updateLeaderboard(contestId)
        return ResponseEntity.noContent().build()
    }
}
