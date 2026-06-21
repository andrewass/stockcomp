package com.stockcomp.participant

import com.stockcomp.participant.internal.ParticipantService
import com.stockcomp.participant.internal.toUserParticipantDto
import org.springframework.stereotype.Service

@Service
class ParticipantServiceExternal(
    private val participantServiceInternal: ParticipantService,
) {
    fun getParticipantsFromContest(contestId: Long): List<UserParticipantDto> =
        participantServiceInternal
            .getAllByContest(contestId)
            .map { toUserParticipantDto(it) }

    fun rankParticipantsForContest(contestId: Long): List<UserParticipantDto> =
        participantServiceInternal
            .rankParticipantsForContest(contestId)
            .map { toUserParticipantDto(it) }
}
