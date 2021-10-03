package com.stockcomp.service.contest

import com.stockcomp.response.ParticipantDto
import com.stockcomp.response.UpcomingContestParticipantDto

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getUpcomingContestsParticipant(username: String): List<UpcomingContestParticipantDto>

    fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto
}