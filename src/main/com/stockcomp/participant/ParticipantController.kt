package com.stockcomp.participant

import com.stockcomp.participant.dto.*
import com.stockcomp.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val tokenService: TokenService,
    private val participantService: ParticipantService,
) {

    @PostMapping("/sign-up")
    fun signUp(
        @RequestParam contestNumber: Int,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<HttpStatus> {
        tokenService.extractEmailFromToken(jwt)
            .let { participantService.signUpParticipant(it, contestNumber) }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/contest")
    fun getParticipantForUser(
        @RequestParam contestNumber: Int,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<ParticipantDto>? =
        tokenService.extractEmailFromToken(jwt)
            .let { participantService.getParticipant(contestNumber, it) }
            ?.let { ResponseEntity.ok(mapToParticipantDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)

    @GetMapping("/active-participants")
    fun getAllActiveParticipantsForUser(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ParticipantDto>> =
        tokenService.extractEmailFromToken(jwt)
            .let { participantService.getActiveParticipants(it) }
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/running-participants")
    fun getAllRunningParticipants(
        @RequestParam symbol: String
    ): ResponseEntity<List<ParticipantDto>> {
        return ResponseEntity.ok(emptyList())
    }

    @GetMapping("/sorted")
    fun getSortedParticipantsForContest(
        @RequestParam contestNumber: Int,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ParticipantPageDto> =
        participantService.getParticipantsSortedByRank(contestNumber, pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToParticipantPageDto(it)) }

    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToHistoricParticipant(it) }
            .let { ResponseEntity.ok(it) }
}