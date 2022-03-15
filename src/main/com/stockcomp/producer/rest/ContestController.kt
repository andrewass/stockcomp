package com.stockcomp.producer.rest

import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.producer.common.CustomExceptionHandler
import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.contest.ContestService
import com.stockcomp.service.security.JwtService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
@Api(description = "Endpoints for contest related operations")
class ContestController(
    private val contestService: ContestService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {


    @GetMapping("/participant")
    @ApiOperation("Get participant of a given contest")
    fun getParticipant(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<ParticipantDto> =
        extractUsernameFromRequest(httpServletRequest)
            .let { contestService.getParticipant(contestNumber, it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/participant-history")
    @ApiOperation("Get participant history for a given user")
    fun getParticipantHistory(
        httpServletRequest: HttpServletRequest, @RequestParam username: String
    ): ResponseEntity<List<ParticipantDto>> =
        contestService.getParticipantHistory(username)
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}