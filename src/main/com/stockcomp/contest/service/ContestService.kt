package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import org.springframework.data.domain.Page

interface ContestService {

    fun createContest(request: CreateContestRequest)

    fun deleteContest(contestNumber: Int)

    fun updateContest(request: UpdateContestRequest)

    fun findByContestNumber(contestNumber: Int): Contest

    fun findByContestNumberAndStatus(status: ContestStatus, contestNumber: Int): Contest

    fun saveContest(contest: Contest)

    fun getActiveContests(): List<Contest>

    fun getRunningContests(): List<Contest>

    fun getCompletedContests(): List<Contest>

    fun getAllContestsSorted(pageNumber: Int, pageSize: Int): Page<Contest>
}