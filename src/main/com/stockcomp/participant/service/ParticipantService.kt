package com.stockcomp.participant.service

import com.stockcomp.participant.dto.ParticipantDto

interface ParticipantService {

    fun getParticipantsSortedByRank(contestNumber: Int): List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto

    fun getParticipantHistory(username: String): List<ParticipantDto>


}