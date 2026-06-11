package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LeaderboardQueryService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
) {
    fun getSortedLeaderboardEntryPage(
        pageNumber: Int,
        pageSize: Int,
    ): LeaderboardEntryPageDto =
        mapToLeaderboardEntryPageDto(
            leaderboardEntryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("ranking"))),
        )

    fun getLeaderboardEntryDtoForUser(userId: Long): LeaderboardEntryDto =
        mapToLeaderboardEntryDto(
            leaderboardEntryRepository.findByUserId(userId)
                ?: throw NoSuchElementException("No leaderboard entry found for user id $userId"),
        )

    fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry> = leaderboardEntryRepository.findAllByOrderByScore()
}
