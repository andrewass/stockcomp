package com.stockcomp.service.contest

import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.ParticipantDto
import com.stockcomp.dto.UpcomingContestParticipantDto

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getAllContests() : List<ContestDto>

    fun getUpcomingContestsParticipant(username: String): List<UpcomingContestParticipantDto>

    fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto>

    fun getParticipantsByRank(contestNumber: Int) : List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto
}