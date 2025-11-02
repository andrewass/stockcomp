package com.stockcomp.contest.internal

import com.stockcomp.contest.internal.ContestStatus.AWAITING_COMPLETION
import com.stockcomp.contest.internal.ContestStatus.AWAITING_START
import com.stockcomp.contest.internal.ContestStatus.COMPLETED
import com.stockcomp.contest.internal.ContestStatus.RUNNING
import com.stockcomp.contest.internal.ContestStatus.STOPPED
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ContestService(
    private val contestRepository: ContestRepository,
) {
    fun createContest(
        contestName: String,
        startTime: LocalDateTime,
        durationDays: Long
    ): Contest =
        Contest(
            contestName = contestName,
            startTime = startTime,
            endTime = startTime.plusDays(durationDays),
        ).also { contestRepository.save(it) }

    fun getContest(contestId: Long): Contest = contestRepository.getReferenceById(contestId)

    fun deleteContest(contestId: Long) {
        contestRepository.deleteByContestId(contestId)
    }

    fun updateContest(
        contestId: Long,
        contestName: String,
        contestStatus: ContestStatus,
        startTime: LocalDateTime,
    ) {
        contestRepository
            .findByContestId(contestId)
            .apply {
                this.contestStatus = contestStatus
                this.contestName = contestName
                this.startTime = startTime
                endTime = this.startTime.plusMonths(2)
            }.also { contestRepository.save(it) }
    }

    fun getActiveContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(
            listOf(RUNNING, STOPPED, AWAITING_START),
        )

    fun existsActiveContest(): Boolean =
        contestRepository.existsByContestStatusIn(listOf(RUNNING, STOPPED, AWAITING_START))

    fun getRunningContests(): List<Contest> = contestRepository.findAllByContestStatusIn(listOf(RUNNING))

    fun getCompletedContests(): List<Contest> = contestRepository.findAllByContestStatusIn(listOf(COMPLETED))

    fun getContestsAwaitingCompletion(): List<Contest> =
        contestRepository.findAllByContestStatusIn(listOf(AWAITING_COMPLETION))

    fun getAllContestsSorted(
        pageNumber: Int,
        pageSize: Int,
    ): Page<Contest> = contestRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("contestId")))

    fun findByContestId(contestId: Long): Contest = contestRepository.findByContestId(contestId)

    fun markContestAsCompleted(contestId: Long) {
        contestRepository
            .findByContestId(contestId)
            .also { it.contestStatus = COMPLETED }
    }

    fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}
