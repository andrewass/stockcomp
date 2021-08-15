package com.stockcomp.service.admin

import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto

interface AdminService {
    fun getRunningAndUpcomingContests(): List<ContestDto>
    fun getUsers(): List<UserDto>
    fun getContest(id: Long): ContestDto
}