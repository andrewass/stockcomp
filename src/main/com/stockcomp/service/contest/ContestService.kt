package com.stockcomp.service.contest

import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.contest.ParticipantDto
import com.stockcomp.dto.contest.ContestParticipantDto

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getAllContests() : List<ContestDto>

    fun getContestParticipantsByStatus(statusList : List<String>, username: String) : List<ContestParticipantDto>

    fun getUpcomingContestsParticipant(username: String): List<ContestParticipantDto>

    fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto>

    fun getParticipantsByRank(contestNumber: Int) : List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto

    fun getParticipantHistory(username: String) : List<ParticipantDto>
}