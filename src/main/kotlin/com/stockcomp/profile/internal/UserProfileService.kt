package com.stockcomp.profile.internal

import com.stockcomp.profile.UserProfileDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserProfileService(
    private val userProfileRepository: UserProfileRepository,
) {
    fun getUserProfile(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): UserProfileDto {
        val identity =
            userProfileRepository.findPublicIdentity(userId)
                ?: throw NoSuchElementException("User with id $userId was not found")

        return UserProfileDto(
            userId = identity.userId,
            username = identity.username,
            fullName = identity.fullName,
            country = identity.country,
            performance = userProfileRepository.getPerformanceSummary(userId),
            leaderboard = userProfileRepository.getLeaderboardStanding(userId),
            contestHistory = userProfileRepository.getContestHistory(userId, pageNumber, pageSize),
        )
    }
}
