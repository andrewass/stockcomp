package com.stockcomp.participant.controller

import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.dto.mapToParticipantDto
import com.stockcomp.participant.service.MaintainParticipantService
import com.stockcomp.participant.service.ParticipantService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val participantService: ParticipantService,
    private val maintainParticipantService: MaintainParticipantService
) {

    @PostMapping("/sign-up-participant")
    fun signUp(@RequestParam contestNumber: Int, @RequestParam ident: String): ResponseEntity<HttpStatus> {
        participantService.signUpParticipant(ident, contestNumber)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/participant-by-contest")
    fun getParticipant(@RequestParam contestNumber: Int, @RequestParam ident: String): ResponseEntity<ParticipantDto>? =
        participantService.getParticipant(contestNumber, ident)
            ?.let { ResponseEntity.ok(mapToParticipantDto(it)) }
            ?: ResponseEntity(HttpStatus.OK)


    @GetMapping("/sorted-participants")
    fun getSortedParticipants(@RequestParam contestNumber: Int): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantsSortedByRank(contestNumber)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/maintain-participants")
    fun maintainParticipants(){
        maintainParticipantService.maintainParticipants()
    }

    @GetMapping("/participant-history")
    fun getParticipantHistory(@RequestParam ident: String): ResponseEntity<List<ParticipantDto>> =
        participantService.getParticipantHistory(ident)
            .map { mapToParticipantDto(it) }
            .let { ResponseEntity.ok(it) }
}