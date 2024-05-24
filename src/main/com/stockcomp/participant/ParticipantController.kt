package com.stockcomp.participant

import com.stockcomp.participant.dto.*
import com.stockcomp.token.TokenClaims
import com.stockcomp.token.TokenData
import com.stockcomp.token.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService,
) {

    @GetMapping("/contest")
    fun getParticipantForUser(
        @RequestParam contestNumber: Int,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<DetailedParticipantDto> =
        participantService.getParticipant(contestNumber, tokenClaims.userIdentification)
            ?.let { ResponseEntity.ok(toDetailedParticipant(it)) }
            ?: ResponseEntity(HttpStatus.NO_CONTENT)

    @GetMapping("/active")
    fun getAllActiveParticipantsForUser(
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<ParticipantDto>> =
        participantService.getActiveParticipants(tokenClaims.userIdentification)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/running-participants")
    fun getAllRunningParticipants(
        @RequestParam symbol: String,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<DetailedParticipantDto>> =
        participantService.getRunningDetailedParticipantsForSymbol(tokenClaims.userIdentification, symbol)
            .let { ResponseEntity.ok(it) }

    @GetMapping("/sorted")
    fun getSortedParticipantsForContest(
        @RequestParam contestNumber: Int,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ParticipantPageDto> =
        participantService.getParticipantsSortedByRank(contestNumber, pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToParticipantPage(it)) }

    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToHistoricParticipant(it) }
            .let { ResponseEntity.ok(it) }
}