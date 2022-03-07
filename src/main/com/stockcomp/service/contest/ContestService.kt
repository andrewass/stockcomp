package com.stockcomp.service.contest

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.dto.contest.ContestParticipantDto

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int): Long?

    fun getContest(contestNumber: Int): ContestDto

    fun getContests(statusList: List<ContestStatus>) : List<ContestDto>

    fun getContestParticipants(statusList: List<ContestStatus>, username: String) : List<ContestParticipantDto>

    fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto>

    fun getParticipantsByRank(contestNumber: Int) : List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto

    fun getParticipantHistory(username: String) : List<ParticipantDto>
}