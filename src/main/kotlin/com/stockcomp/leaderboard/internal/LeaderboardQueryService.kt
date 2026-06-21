package com.stockcomp.leaderboard.internal

import com.stockcomp.leaderboard.LeaderboardEntryDto
import com.stockcomp.leaderboard.LeaderboardEntryPageDto
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserServiceExternal
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LeaderboardQueryService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val userService: UserServiceExternal,
) {
    fun getSortedLeaderboardEntryPage(
        pageNumber: Int,
        pageSize: Int,
    ): LeaderboardEntryPageDto {
        val entryPage = leaderboardEntryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("ranking")))
        val userDetailsById = getUserDetailsById(entryPage.content.map { it.userId })

        return mapToLeaderboardEntryPageDto(entryPage, userDetailsById)
    }

    fun getLeaderboardEntryDtoForUser(userId: Long): LeaderboardEntryDto {
        val entry =
            leaderboardEntryRepository.findByUserId(userId)
                ?: throw NoSuchElementException("No leaderboard entry found for user id $userId")
        val userDetails =
            getUserDetailsById(listOf(entry.userId))[entry.userId]
                ?: throw NoSuchElementException("No user details found for user id ${entry.userId}")

        return mapToLeaderboardEntryDto(entry, userDetails)
    }

    private fun getUserDetailsById(userIds: List<Long>): Map<Long, UserDetailsDto> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }
        return userService.getUserDetails(userIds.distinct()).associateBy { it.userId }
    }
}
