package com.stockcomp.leaderboard.leaderboard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository
) {

    fun getSortedLeaderboardEntries(pageNumber: Int, pageSize: Int): Page<LeaderboardEntry> =
        leaderboardEntryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("ranking")))

    fun getLeaderboardEntryForUser(userId: Long): LeaderboardEntry =
        leaderboardEntryRepository.findByUserId(userId)

    fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry> =
        leaderboardEntryRepository.findAllByOrderByScore()

    fun saveEntry(leaderboardEntry: LeaderboardEntry) {
        leaderboardEntryRepository.save(leaderboardEntry)
    }

    fun saveAllEntries(leaderboardEntries: List<LeaderboardEntry>) {
        leaderboardEntryRepository.saveAll(leaderboardEntries)
    }
}