package com.stockcomp.controller

import com.stockcomp.IntegrationTest
import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@WithMockUser
@Transactional
@AutoConfigureMockMvc
internal class TaskControllerTest : IntegrationTest() {

    @Autowired
    lateinit var contestRepository: ContestRepository

    @Test
    fun `should start contest`() {
        createContest(ContestStatus.AWAITING_START)
    }

    @Test
    fun `should stop running contest`() {
        createContest(ContestStatus.RUNNING)
    }

    @Test
    fun `should start order processing`() {
        createContest(ContestStatus.RUNNING)
    }

    @Test
    fun `should stop order processing`() {
        createContest(ContestStatus.STOPPED)
    }


    private fun createContest(contestStatus: ContestStatus) {
        val contest = Contest(
            contestNumber = 1, startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusMonths(2), contestStatus = contestStatus
        )
        contestRepository.save(contest)
    }
}