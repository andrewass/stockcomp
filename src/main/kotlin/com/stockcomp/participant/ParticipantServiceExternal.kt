package com.stockcomp.participant

interface ParticipantServiceExternal {
    fun getParticipantsFromContest(contestId: Long): List<UserParticipantDto>

    fun rankParticipantsForContest(contestId: Long): List<UserParticipantDto>
}
