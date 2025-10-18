package com.stockcomp.participant

import org.springframework.stereotype.Service

@Service
class ParticipantServiceExternal(
    private val participantServiceInternal: ParticipantService
) {

    fun getParticipantsFromContest(contestId: Long): List<UserParticipantDto> =
        participantServiceInternal.getAllByContest(contestId)
            .map { toUserParticipantDto(it) }
}