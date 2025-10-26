package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class LeaderboardQueryService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
) {
    fun getSortedLeaderboardEntries(
        pageNumber: Int,
        pageSize: Int,
    ): Page<LeaderboardEntry> = leaderboardEntryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("ranking")))

    fun getLeaderboardEntryForUser(userId: Long): LeaderboardEntry = leaderboardEntryRepository.findByUserId(userId)

    fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry> = leaderboardEntryRepository.findAllByOrderByScore()
}
