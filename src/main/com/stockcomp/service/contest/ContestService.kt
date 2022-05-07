package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.participant.entity.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.participant.dto.ContestParticipantDto
import com.stockcomp.dto.contest.ParticipantDto

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