package com.stockcomp.producer.rest

import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.participant.ParticipantService
import com.stockcomp.service.security.DefaultJwtService
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val participantService: ParticipantService,
    private val defaultJwtService: DefaultJwtService
) {

    @GetMapping("/total-investment-value")
    @ApiOperation(value = "Get total investment value for participant")
    fun getTotalInvestmentValue(
        httpServletRequest: HttpServletRequest, @RequestParam contestNumber: Int
    ): ResponseEntity<Double> =
        extractUsernameFromRequest(httpServletRequest)
            .let { participantService.getTotalValue(it, contestNumber) }
            .let { ResponseEntity.ok(it) }


    private fun extractUsernameFromRequest(request: HttpServletRequest): String =
        getAccessTokenFromCookie(request)
            .let { defaultJwtService.extractUsername(it!!) }
}