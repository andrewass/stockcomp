package com.stockcomp.producer.rest

import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.producer.common.CustomExceptionHandler
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.contest.ContestService
import com.stockcomp.service.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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