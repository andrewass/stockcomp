package com.stockcomp.participant

import com.stockcomp.participant.dto.*
import com.stockcomp.participant.presentation.*
import com.stockcomp.token.TokenClaims
import com.stockcomp.token.TokenData
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {

    @PostMapping("/sign-up")
    fun signUpParticipant(
        @RequestParam userId: Long,
        @RequestParam contestId: Long
    ) {
        participantService.signUpParticipant(userId = userId, contestId = contestId)
    }

    @GetMapping("/{participantId}")
    fun getParticipant(
        @PathVariable participantId: Long
    ): ResponseEntity<DetailedParticipantDto> =
        participantService.getParticipant(participantId)
            .let { ResponseEntity.ok(toDetailedParticipant(it)) }

    @GetMapping("/active")
    fun getAllActiveParticipantsForUser(
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<ParticipantDto>> =
        participantService.getActiveParticipants(tokenClaims.userId)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/running")
    fun getAllRunningParticipants(
        @RequestParam symbol: String,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<DetailedParticipantDto>> =
        participantService.getRunningDetailedParticipantsForSymbol(tokenClaims.userId, symbol)
            .let { ResponseEntity.ok(it) }

    @GetMapping("/sorted")
    fun getSortedParticipantsForContest(
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