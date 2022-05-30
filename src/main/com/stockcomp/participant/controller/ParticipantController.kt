package com.stockcomp.participant.controller

import com.stockcomp.authentication.controller.getAccessTokenFromCookie
import com.stockcomp.authentication.service.JwtService
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.dto.mapToParticipantDto
import com.stockcomp.participant.service.ParticipantService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val participantService: ParticipantService,
    private val jwtService: JwtService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestParam contestNumber: Int, servletRequest: HttpServletRequest): ResponseEntity<HttpStatus> {
        participantService.signUpParticipant(extractUsernameFromRequest(servletRequest), contestNumber)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/by-contest")
    fun getParticipant(
        servletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<ParticipantDto> =
        extractUsernameFromRequest(servletRequest)
            .let { participantService.getParticipant(contestNumber, it) }
            .let { ResponseEntity.ok(mapToParticipantDto(it)) }


    @GetMapping("/sorted-participants")
    fun getSortedParticipants(@RequestParam contestNumber: Int): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantsSortedByRank(contestNumber)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/participant-history")
    fun getParticipantHistory(
        servletRequest: HttpServletRequest, @RequestParam username: String
    ): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(servletRequest: HttpServletRequest): String =
        getAccessTokenFromCookie(servletRequest)
            .let { jwtService.extractUsername(it!!) }
}