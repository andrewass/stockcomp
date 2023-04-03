package com.stockcomp.participant.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant
import org.springframework.data.domain.Page

interface ParticipantService {

    fun getParticipantsSortedByRank(contestNumber: Int, pageNumber: Int, pageSize: Int): Page<Participant>

    fun getParticipant(contestNumber: Int, email: String): Participant?

    fun getAllByContest(contest: Contest): List<Participant>

    fun getAllByEmailAndContest(email: String, contest: Contest): List<Participant>

    fun getParticipantHistory(username: String): List<Participant>

    fun signUpParticipant(email: String, contestNumber: Int)

    fun saveParticipant(participant: Participant)

    fun getActiveParticipantsByUser(username: String): List<Participant>

    fun maintainParticipantInvestmentValues(contest: Contest)

    fun maintainParticipantRanking(contest: Contest)
}