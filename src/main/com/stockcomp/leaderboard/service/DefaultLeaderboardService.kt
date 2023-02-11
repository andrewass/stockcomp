package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.repository.LeaderboardEntryRepository
import com.stockcomp.user.entity.User
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


    override fun getLeaderboardEntryForUserIdent(ident: String): LeaderboardEntry? =
        userService.findUserByUsername(ident)
            .let { leaderboardEntryRepository.findByUser(it) }


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