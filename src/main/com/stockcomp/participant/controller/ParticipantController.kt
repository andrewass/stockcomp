package com.stockcomp.participant.controller

import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.service.ParticipantService
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val participantService: ParticipantService,
    private val jwtService: JwtService
) {

    @GetMapping("/by-contest")
    fun getParticipant(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<ParticipantDto> =
        extractUsernameFromRequest(httpServletRequest)
            .let { participantService.getParticipant(contestNumber, it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/sorted-participants")
    fun getSortedParticipants(@RequestParam contestNumber: Int) : ResponseEntity<List<ParticipantDto>> =
        ResponseEntity.ok(participantService.getParticipantsSortedByRank(contestNumber))


    @GetMapping("/participant-history")
    fun getParticipantHistory(
        httpServletRequest: HttpServletRequest, @RequestParam username: String
    ): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantHistory(username)
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { jwtService.extractUsername(it!!) }
}