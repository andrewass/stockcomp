package com.stockcomp.participant.participant

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.contest.ContestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {

    /**
     * Sign up a participant for a given contest
     */
    @PostMapping("/sign-up/{contestId}")
    fun signUpParticipant(
        @PathVariable contestId: Long,
        @TokenData tokenClaims: TokenClaims
    ) {
        participantService.signUpParticipant(userId = tokenClaims.userId, contestId = contestId)
    }

    /**
     * Get all the signed up participants for a given user
     */
    @GetMapping("/registered")
    fun registeredParticipant(
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<ContestParticipantDto>> =
        ResponseEntity.ok(participantService.getParticipatingContests(tokenClaims.userId))

    /**
     * Get all the contests the user has not signed up for
     */
    @GetMapping("/unregistered")
    fun unregisteredParticipant(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<List<ContestDto>> =
        ResponseEntity.ok(participantService.getUnregisteredContests(tokenClaims.userId))

    /**
     * Get all the active participants for a given user, including investment and orders for the given symbol
     */
    @GetMapping("/detailed/symbol/{symbol}")
    fun getDetailedParticipantsForSymbol(
        @PathVariable symbol: String,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<List<DetailedParticipantDto>> =
        ResponseEntity.ok(participantService.getDetailedParticipantsForSymbol(tokenClaims.userId, symbol))

    /**
     * Get a given participant, including investment and orders
     */
    @GetMapping("/detailed/contest/{contestId}")
    fun getDetailedParticipantForContest(
        @PathVariable contestId: Long,
        @TokenData tokenClaims: TokenClaims
    ): ResponseEntity<DetailedParticipantDto> =
        ResponseEntity.ok(participantService.getDetailedParticipantForContest(contestId, tokenClaims.userId))

    /**
     * Get sorted participants for a given contest
     */
    @GetMapping("/sorted")
    fun getSortedParticipantWithUserDetailsForContest(
        @RequestParam contestId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ParticipantPageDto> =
        participantService.getParticipantsSortedByRank(contestId, pageNumber, pageSize)
            .let { ResponseEntity.ok(toParticipantPage(it)) }

    /**
     * Get participant history for a given username
     */
    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToHistoricParticipant(it) }
            .let { ResponseEntity.ok(it) }
}
