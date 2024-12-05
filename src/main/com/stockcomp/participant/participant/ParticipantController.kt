package com.stockcomp.participant.participant

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.contest.ContestDto
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {

    @PostMapping("/sign-up/{contestId}")
    fun signUpParticipant(
        @PathVariable contestId: Long,
        @TokenData tokenClaims: TokenClaims
    ) {
        participantService.signUpParticipant(userId = tokenClaims.userId, contestId = contestId)
    }

    @GetMapping("/registered")
    fun registeredParticipant(
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<ContestParticipantDto>> =
        ResponseEntity.ok(participantService.getRegisteredParticipatingContests(tokenClaims.userId))

    @GetMapping("/symbol")
    fun getParticipantsForSymbol(
        @RequestParam symbol: String,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<DetailedParticipantDto>> =
        ResponseEntity.ok(participantService.getRunningDetailedParticipantsForSymbol(tokenClaims.userId, symbol))

    @GetMapping("/unregistered")
    fun unregisteredParticipant(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<List<ContestDto>> =
        ResponseEntity.ok(participantService.getUnregisteredContests(tokenClaims.userId))

    @GetMapping("/{participantId}")
    fun getParticipant(
        @PathVariable participantId: Long
    ): ResponseEntity<DetailedParticipantDto> =
        participantService.getParticipant(participantId)
            .let { ResponseEntity.ok(toDetailedParticipant(it)) }

    @GetMapping("/sorted")
    fun getSortedParticipantWithUserDetailsForContest(
        @RequestParam contestId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ParticipantPageDto> =
        participantService.getParticipantsSortedByRank(contestId, pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToParticipantPage(it)) }

    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToHistoricParticipant(it) }
            .let { ResponseEntity.ok(it) }
}
