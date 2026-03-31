package com.stockcomp.participant.internal.investment

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.participant.InvestmentDto
import com.stockcomp.participant.mapToInvestmentDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/participants/investments")
class InvestmentController(
    private val investmentService: InvestmentProcessingService,
) {
    @GetMapping("/all")
    fun getAllFromContest(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<List<InvestmentDto>> =
        ResponseEntity.ok(
            investmentService
                .getInvestmentsForParticipant(
                    contestId = contestId,
                    userId = tokenClaims.userId,
                ).map { mapToInvestmentDto(it) },
        )

    @GetMapping
    fun getInvestmentBySymbolAndContest(
        @TokenData tokenClaims: TokenClaims,
        @RequestParam @NotBlank symbol: String,
        @RequestParam @Positive contestId: Long,
    ): ResponseEntity<InvestmentDto> {
        val investment =
            investmentService.getInvestmentForSymbol(
                contestId = contestId,
                symbol = symbol,
                userId = tokenClaims.userId,
            )
        return if (investment != null) {
            ResponseEntity.ok(mapToInvestmentDto(investment))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
