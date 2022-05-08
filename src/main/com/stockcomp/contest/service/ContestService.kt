package com.stockcomp.contest.service

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.dto.ContestParticipantDto

interface ContestService {

    fun createContest(request: CreateContestRequest): ContestDto

    fun getContest(contestNumber: Int): ContestDto

    fun deleteContest(contestNumber: Int)

    fun updateContest(updateContestRequest: UpdateContestRequest) : ContestDto

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)


    fun signUpUser(username: String, contestNumber: Int): Long?


    fun getContests(statusList: List<ContestStatus>): List<ContestDto>

    fun getContestParticipants(statusList: List<ContestStatus>, username: String): List<ContestParticipantDto>

}