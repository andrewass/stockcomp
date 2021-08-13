package com.stockcomp.service.admin

import com.stockcomp.response.ContestDto

interface AdminService {

    fun getRunningAndUpcomingContests(): List<ContestDto>

}