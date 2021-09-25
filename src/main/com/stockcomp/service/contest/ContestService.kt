package com.stockcomp.service.contest

import com.stockcomp.domain.contest.Contest
import com.stockcomp.response.ParticipantDto
import com.stockcomp.response.UpcomingContestDto

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getUpcomingContests(username: String): List<UpcomingContestDto>

    fun getParticipantsByTotalValue(contestNumber: Int): List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String): ParticipantDto
}