package com.stockcomp.service.admin

import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.request.ContestUpdateRequest

interface AdminService {

    fun getAllContests(): List<ContestDto>

    fun getUsers(): List<UserDetailsDto>

    fun getContest(id: Long): ContestDto

    fun updateContestStatus(request: ContestUpdateRequest): ContestDto

    fun createContest(request: ContestUpdateRequest): ContestDto

    fun deleteContest(id: Long): ContestDto

    fun getCompletedContests(): List<ContestDto>
}