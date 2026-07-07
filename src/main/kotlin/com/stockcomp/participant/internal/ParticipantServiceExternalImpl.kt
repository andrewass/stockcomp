package com.stockcomp.participant.internal

import com.stockcomp.participant.ParticipantServiceExternal
import com.stockcomp.participant.UserParticipantDto
import org.springframework.stereotype.Service

@Service
class ParticipantServiceExternalImpl(
    private val participantService: ParticipantService,
) : ParticipantServiceExternal {
    override fun getParticipantsFromContest(contestId: Long): List<UserParticipantDto> =
        participantService
            .getAllByContest(contestId)
            .map { toUserParticipantDto(it) }

    override fun rankParticipantsForContest(contestId: Long): List<UserParticipantDto> =
        participantService
            .rankParticipantsForContest(contestId)
            .map { toUserParticipantDto(it) }
}
