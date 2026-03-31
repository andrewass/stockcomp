package com.stockcomp.participant.internal

import com.stockcomp.common.TokenClaims
import com.stockcomp.common.TokenData
import com.stockcomp.contest.ContestDto
import com.stockcomp.participant.CommonParticipantPageDto
import com.stockcomp.participant.ContestParticipantDto
import com.stockcomp.participant.DetailedParticipantDto
import com.stockcomp.participant.HistoricParticipantDto
import com.stockcomp.participant.SignUpParticipantRequest
import com.stockcomp.participant.UserParticipantDto
import com.stockcomp.participant.mapToHistoricParticipant
import com.stockcomp.participant.toParticipantPage
import com.stockcomp.participant.toUserParticipantDto
import com.stockcomp.user.UserServiceExternal
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService,
    private val userService: UserServiceExternal,
) {
    /**
     * Sign up a participant for a given contest
     */
    @PostMapping("/sign-up")
    fun signUpParticipant(
        @Valid @RequestBody request: SignUpParticipantRequest,
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<UserParticipantDto> =
        ResponseEntity.ok(
            toUserParticipantDto(participantService.signUpParticipant(userId = tokenClaims.userId, contestId = request.contestId)),
        )

    /**
     * Get all the signed up participants for a given user
     */
    @GetMapping("/registered")
    fun registeredParticipant(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<List<ContestParticipantDto>> = ResponseEntity.ok(participantService.getParticipatingContests(tokenClaims.userId))

    /**
     * Get all the contests the user has not signed up for
     */
    @GetMapping("/unregistered")
    fun unregisteredParticipant(
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<List<ContestDto>> = ResponseEntity.ok(participantService.getNonParticipatingContests(tokenClaims.userId))

    /**
     * Get all the active participants for a given user, including investment and orders for the given symbol
     */
    @GetMapping("/detailed/symbol/{symbol}")
    fun getDetailedParticipantsForSymbol(
        @PathVariable @NotBlank symbol: String,
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<List<DetailedParticipantDto>> =
        ResponseEntity.ok(participantService.getDetailedParticipantsForSymbol(tokenClaims.userId, symbol))

    /**
     * Get a given participant, including investment and orders
     */
    @GetMapping("/detailed/contest/{contestId}")
    fun getDetailedParticipantForContest(
        @PathVariable @Positive contestId: Long,
        @TokenData tokenClaims: TokenClaims,
    ): ResponseEntity<DetailedParticipantDto> {
        val participant = participantService.getDetailedParticipantForContest(contestId, tokenClaims.userId)
        return if (participant != null) {
            ResponseEntity.ok(participant)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    /**
     * Get sorted participants for a given contest
     */
    @GetMapping("/sorted")
    fun getSortedParticipantWithUserDetailsForContest(
        @RequestParam @Positive contestId: Long,
        @RequestParam @PositiveOrZero pageNumber: Int,
        @RequestParam @Positive pageSize: Int,
    ): ResponseEntity<CommonParticipantPageDto> {
        val participantPage = participantService.getParticipantsSortedByRank(contestId, pageNumber, pageSize)
        val userDetails = userService.getUserDetails(participantPage.content.map { it.userId })

        return ResponseEntity.ok(toParticipantPage(participantPage.content, userDetails, participantPage.totalElements))
    }

    /**
     * Get participant history for a given username
     */
    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam @NotBlank username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        ResponseEntity.ok(participantService.getParticipantHistory(username).map { mapToHistoricParticipant(it) })
}
