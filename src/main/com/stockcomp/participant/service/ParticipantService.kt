package com.stockcomp.participant.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant

interface ParticipantService {

    fun getParticipantsSortedByRank(contestNumber: Int): List<Participant>

    fun getParticipant(contestNumber: Int, email: String): Participant?

    fun getAllByContest(contest: Contest) : List<Participant>

    fun getAllByUsernameAndContest(username: String, contest: Contest): List<Participant>

    fun getParticipantHistory(username: String): List<Participant>

    fun signUpParticipant(email: String, contestNumber: Int)

    fun saveParticipant(participant: Participant)

    fun getActiveParticipantsByUser(username: String) : List<Participant>
}