package com.stockcomp.participant.participant

import com.stockcomp.contest.ContestDto
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {

    @PostMapping("/sign-up")
    fun signUpParticipant(
        @RequestParam userId: Long,
        @RequestParam contestId: Long
    ) {
        participantService.signUpParticipant(userId = userId, contestId = contestId)
    }

    @GetMapping("/registered/{userId}")
    fun registeredParticipant(
        @PathVariable userId: Long,
    ): ResponseEntity<List<ContestParticipantDto>> =
        ResponseEntity.ok(participantService.getRegisteredParticipatingContests(userId))

    @GetMapping("/unregistered/{userId}")
    fun unregisteredParticipant(
        @PathVariable userId: Long,
    ): ResponseEntity<List<ContestDto>> =
        ResponseEntity.ok(participantService.getUnregisteredContests(userId))

    @GetMapping("/{participantId}")
    fun getParticipant(
        @PathVariable participantId: Long
    ): ResponseEntity<DetailedParticipantDto> =
        participantService.getParticipant(participantId)
            .let { ResponseEntity.ok(toDetailedParticipant(it)) }

    @GetMapping("/sorted")
    fun getSortedParticipantsForContest(
        @RequestParam contestId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<ParticipantPageDto> =
        participantService.getParticipantsSortedByRank(contestId, pageNumber, pageSize)
            .let { ResponseEntity.ok(mapToParticipantPage(it)) }

    @GetMapping("/history")
    fun getDetailedParticipantHistoryForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<HistoricParticipantDto>> =
        participantService.getParticipantHistory(username)
            .map { mapToHistoricParticipant(it) }
            .let { ResponseEntity.ok(it) }
}