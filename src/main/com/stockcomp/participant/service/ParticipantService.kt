package com.stockcomp.participant.service

import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.entity.Investment
import com.stockcomp.participant.entity.Participant

interface ParticipantService {

    fun getSortedParticipantsByRank(contestNumber: Int): List<Participant>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto

    fun getParticipantHistory(username: String): List<ParticipantDto>

    fun getInvestmentForSymbol(username: String, contestNumber: Int, symbol: String): Investment?

    fun getAllInvestmentsForContest(username: String, contestNumber: Int): List<Investment>
}