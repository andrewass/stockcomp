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
        durationDays: Long,
    ): Contest =
        Contest(
            contestName = contestName,
            startTime = startTime,
            endTime = startTime.plusDays(durationDays),
        ).also { contestRepository.save(it) }

    fun getContest(contestId: Long): Contest = findContestByIdOrThrow(contestId)

    fun deleteContest(contestId: Long) {
        findContestByIdOrThrow(contestId)
        contestRepository.deleteById(contestId)
    }

    fun updateContest(
        contestId: Long,
        contestName: String?,
        contestStatus: ContestStatus?,
        startTime: LocalDateTime?,
    ): Contest =
        findContestByIdOrThrow(contestId)
            .apply {
                if (startTime != null) {
                    if (this.contestStatus != AWAITING_START) {
                        throw IllegalStateException("Cannot update startTime unless contest status is AWAITING_START")
                    }
                    this.updateStartTimePreservingDuration(startTime)
                }
                contestStatus?.let { this.updateContestStatus(it) }
                contestName?.let { this.renameContest(it) }
            }.also { contestRepository.save(it) }

    fun getActiveContests(): List<Contest> =
        contestRepository.findAllByContestStatusIn(
            listOf(RUNNING, STOPPED, AWAITING_START),
        )

    fun existsActiveContest(): Boolean = contestRepository.existsByContestStatusIn(listOf(RUNNING, STOPPED, AWAITING_START))

    fun getRunningContests(): List<Contest> = contestRepository.findAllByContestStatusIn(listOf(RUNNING))

    fun getContestsAwaitingCompletion(): List<Contest> = contestRepository.findAllByContestStatusIn(listOf(AWAITING_COMPLETION))

    fun getAllContestsSorted(
        pageNumber: Int,
        pageSize: Int,
    ): Page<Contest> = contestRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("contestId")))

    fun markContestAsCompleted(contestId: Long) {
        findContestByIdOrThrow(contestId).also { it.setContestAsCompleted() }
    }

    private fun findContestByIdOrThrow(contestId: Long): Contest =
        contestRepository
            .findById(contestId)
            .orElseThrow { NoSuchElementException("Contest with id $contestId does not exist") }
}
