package com.stockcomp.contest

import com.stockcomp.contest.service.ContestServiceInternal
import org.springframework.stereotype.Service

@Service
class ContestServicePublic(
    private val contestService: ContestServiceInternal
) {

    fun getRunningContests(): List<Int> =
        contestService.getRunningContests().map { it.contestNumber }

    fun getActiveContests(): List<Int> =
        contestService.getActiveContests().map { it.contestNumber }
}