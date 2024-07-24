package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.LeaderboardEntryRepository
import com.stockcomp.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val userService: UserService
) : LeaderboardService {

    override fun getSortedLeaderboardEntries(pageNumber: Int, pageSize: Int): Page<LeaderboardEntry> =
        leaderboardEntryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("ranking")))


    override fun getLeaderboardEntryForEmail(email: String): LeaderboardEntry? =
        userService.findUserByEmail(email)
            ?.let { leaderboardEntryRepository.findByUser(it) }


    override fun getLeaderboardEntryForUser(user: User): LeaderboardEntry? =
        leaderboardEntryRepository.findByUser(user)


    override fun getLeaderboardentriesByOrderByScore(): List<LeaderboardEntry> =
        leaderboardEntryRepository.findAllByOrderByScore()


    override fun saveEntry(leaderboardEntry: LeaderboardEntry) {
        leaderboardEntryRepository.save(leaderboardEntry)
    }

    override fun saveAllEntries(leaderboardEntries: List<LeaderboardEntry>) {
        leaderboardEntryRepository.saveAll(leaderboardEntries)
    }
}