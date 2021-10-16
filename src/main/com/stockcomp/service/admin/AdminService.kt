package com.stockcomp.service.admin

import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto

interface AdminService {

    fun getAllContests(): List<ContestDto>

    fun getUsers(): List<UserDto>

    fun getContest(id: Long): ContestDto

    fun updateContest(contestDto: ContestDto): ContestDto

    fun createContest(request: CreateContestRequest): ContestDto

    fun deleteContest(id: Long): ContestDto

    fun getCompletedContests(): List<ContestDto>
}