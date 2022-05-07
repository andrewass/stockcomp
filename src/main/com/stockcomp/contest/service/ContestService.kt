package com.stockcomp.contest.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.dto.ContestParticipantDto
import com.stockcomp.participant.dto.ParticipantDto
import com.stockcomp.participant.entity.Participant

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int): Long?

    fun getContest(contestNumber: Int): Contest

    fun getContests(statusList: List<ContestStatus>): List<Contest>

    fun getContestParticipants(statusList: List<ContestStatus>, username: String): List<ContestParticipantDto>

    fun getSortedParticipantsByRank(contestNumber: Int): List<Participant>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto

    fun getParticipantHistory(username: String): List<ParticipantDto>
}