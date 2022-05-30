package com.stockcomp.participant.service

import com.stockcomp.participant.entity.Participant

interface ParticipantService {

    fun getParticipantsSortedByRank(contestNumber: Int): List<Participant>

    fun getParticipant(contestNumber: Int, username: String): Participant

    fun getParticipantHistory(username: String): List<Participant>

    fun signUpParticipant(username: String, contestNumber: Int)

    fun saveParticipant(participant: Participant)
}