package com.stockcomp.service.admin

import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.request.CreateContestRequest

interface AdminService {

    fun getAllContests(): List<ContestDto>

    fun getUsers(): List<UserDetailsDto>

    fun getContest(id: Long): ContestDto

    fun updateContestStatus(contestDto: ContestDto): ContestDto

    fun createContest(request: CreateContestRequest): ContestDto

    fun deleteContest(id: Long): ContestDto

    fun getCompletedContests(): List<ContestDto>
}