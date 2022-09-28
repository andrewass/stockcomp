package com.stockcomp.participant.controller

import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.dto.InvestmentDto
import com.stockcomp.participant.dto.mapToInvestmentDto
import com.stockcomp.participant.service.InvestmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/investment")
class InvestmentController(
    private val investmentService: InvestmentService,
) {

    @GetMapping("/get-all")
    fun getAllFromContest(
        @RequestParam contestNumber: Int, @RequestParam ident: String
    ): ResponseEntity<List<InvestmentDto>> =
        investmentService.getAllInvestmentsForParticipant(ident, contestNumber)
            .map { mapToInvestmentDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/get-by-symbol")
    fun getAllFromContest(
        @RequestBody getInvestmentBySymbolRequest: GetInvestmentBySymbolRequest
    ): ResponseEntity<InvestmentDto?> =
        investmentService.getInvestmentForSymbol(getInvestmentBySymbolRequest)
            ?.let { ResponseEntity.ok(mapToInvestmentDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)
}