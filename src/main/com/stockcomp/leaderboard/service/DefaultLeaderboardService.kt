package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.repository.LeaderboardEntryRepository
import com.stockcomp.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val userService: UserService
) : LeaderboardService {

    override fun getSortedLeaderboardEntries(): List<LeaderboardEntry> =
        leaderboardEntryRepository.findAllByOrderByRanking()


    override fun getLeaderboardEntryForUser(username: String): LeaderboardEntry? =
        userService.findUserByUsername(username)
            .let { leaderboardEntryRepository.findByUser(it) }
}