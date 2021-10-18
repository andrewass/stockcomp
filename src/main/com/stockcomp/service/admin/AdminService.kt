package com.stockcomp.service.admin

import com.stockcomp.request.CreateContestRequest
import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.UserDto

interface AdminService {

    fun getAllContests(): List<ContestDto>

    fun getUsers(): List<UserDto>

    fun getContest(id: Long): ContestDto

    fun updateContest(contestDto: ContestDto): ContestDto

    fun createContest(request: CreateContestRequest): ContestDto

    fun deleteContest(id: Long): ContestDto

    fun getCompletedContests(): List<ContestDto>
}