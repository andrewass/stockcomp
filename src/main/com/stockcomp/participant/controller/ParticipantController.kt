package com.stockcomp.participant.controller

import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.dto.mapToParticipantDto
import com.stockcomp.participant.service.MaintainParticipantService
import com.stockcomp.participant.service.ParticipantService
import com.stockcomp.token.service.TokenService
import com.stockcomp.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val tokenService: TokenService,
    private val userService: UserService,
    private val contestService: ContestService,
    private val participantService: ParticipantService,
    private val maintainParticipantService: MaintainParticipantService
) {

    @PostMapping("/sign-up-participant")
    fun signUp(
        @RequestParam contestNumber: Int, @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<HttpStatus> {
        tokenService.extractEmailFromToken(jwt)
            .let { userService.findUserByTokenClaim(it) }
            .let { participantService.signUpParticipant(it.email, contestNumber) }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/participant-by-contest")
    fun getParticipant(
        @RequestParam contestNumber: Int, @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<ParticipantDto>? =
        tokenService.extractEmailFromToken(jwt)
            .let { userService.findUserByTokenClaim(it) }
            .let { participantService.getParticipant(contestNumber, it.email) }
            ?.let { ResponseEntity.ok(mapToParticipantDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)


    @GetMapping("/participant-by-active-contest")
    fun getActiveParticipant(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<ParticipantDto>? {
        val email = tokenService.extractEmailFromToken(jwt)

        return tokenService.extractEmailFromToken(jwt)
            .let { contestService.getActiveContest() }
            ?.let { participantService.getParticipant(it.contestNumber, email) }
            ?.let { ResponseEntity.ok(mapToParticipantDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
    }


    @GetMapping("/sorted-participants")
    fun getSortedParticipants(@RequestParam contestNumber: Int): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantsSortedByRank(contestNumber)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/maintain-participants")
    fun maintainParticipants() {
        maintainParticipantService.maintainParticipants()
    }

    @GetMapping("/participant-history")
    fun getParticipantHistory(@RequestParam ident: String): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantHistory(ident)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }
}