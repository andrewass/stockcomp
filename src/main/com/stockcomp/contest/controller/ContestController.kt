package com.stockcomp.contest.controller

import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.producer.common.CustomExceptionHandler
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.contest.service.ContestService
import com.stockcomp.service.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
class ContestController(
    private val contestService: ContestService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {


    @GetMapping("/participant")
    fun getParticipant(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<ParticipantDto> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getParticipant(contestNumber, it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/participant-history")
    fun getParticipantHistory(
        httpServletRequest: HttpServletRequest, @RequestParam username: String
    ): ResponseEntity<List<ParticipantDto>> =
        contestService.getParticipantHistory(username)
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}