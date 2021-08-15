package com.stockcomp.service.admin

import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto

interface AdminService {
    fun getRunningAndUpcomingContests(): List<ContestDto>
    fun getUsers(): List<UserDto>
    fun getContest(id: Long): ContestDto
    fun updateContest(contestDto: ContestDto)
    fun createContest(request: CreateContestRequest)
}