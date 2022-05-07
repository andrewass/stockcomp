package com.stockcomp.service.admin

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest

interface AdminService {

    fun getAllContests(): List<ContestDto>

    fun getUsers(): List<UserDetailsDto>

    fun getContest(id: Long): ContestDto

    fun updateContestStatus(request: UpdateContestRequest): ContestDto

    fun createContest(request: CreateContestRequest): ContestDto

    fun deleteContest(id: Long): ContestDto

    fun getCompletedContests(): List<ContestDto>
}