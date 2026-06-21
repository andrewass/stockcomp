package com.stockcomp.profile.internal

import com.stockcomp.profile.ContestHistoryPageDto
import com.stockcomp.profile.ContestPerformanceDto
import com.stockcomp.profile.LeaderboardStandingDto
import com.stockcomp.profile.UserPerformanceSummaryDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Repository
class UserProfileRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun findPublicIdentity(userId: Long): PublicUserIdentity? =
        jdbcTemplate
            .query(
                """
                select user_id, username, full_name, country
                from t_user
                where user_id = ?
                """.trimIndent(),
                { resultSet, _ ->
                    PublicUserIdentity(
                        userId = resultSet.getLong("user_id"),
                        username = resultSet.getString("username"),
                        fullName = resultSet.getString("full_name"),
                        country = resultSet.getString("country"),
                    )
                },
                userId,
            ).singleOrNull()

    fun getPerformanceSummary(userId: Long): UserPerformanceSummaryDto =
        jdbcTemplate.queryForObject(
            """
            select
                count(*) as completed_contests,
                count(*) filter (where p.participant_rank = 1) as wins,
                count(*) filter (where p.participant_rank between 1 and 3) as podiums,
                coalesce(round(avg(p.participant_rank)::numeric, 2), 0.00) as average_rank,
                coalesce(
                    round(
                        avg(((p.total_value - $STARTING_PORTFOLIO_VALUE) / $STARTING_PORTFOLIO_VALUE) * 100)::numeric,
                        2
                    ),
                    0.00
                ) as average_return_percentage
            from t_participant p
            join t_contest c on c.contest_id = p.contest_id
            where p.user_id = ?
              and c.contest_status = 'COMPLETED'
            """.trimIndent(),
            { resultSet, _ ->
                UserPerformanceSummaryDto(
                    completedContests = resultSet.getInt("completed_contests"),
                    wins = resultSet.getInt("wins"),
                    podiums = resultSet.getInt("podiums"),
                    averageRank = resultSet.getBigDecimal("average_rank").scaled(),
                    averageReturnPercentage = resultSet.getBigDecimal("average_return_percentage").scaled(),
                )
            },
            userId,
        )

    fun getLeaderboardStanding(userId: Long): LeaderboardStandingDto =
        jdbcTemplate
            .query(
                """
                select
                    le.ranking,
                    le.score,
                    count(m.medal_id) filter (where m.medal_value = 'GOLD') as gold_medals,
                    count(m.medal_id) filter (where m.medal_value = 'SILVER') as silver_medals,
                    count(m.medal_id) filter (where m.medal_value = 'BRONZE') as bronze_medals
                from t_leaderboard_entry le
                left join t_medal m on m.leaderboard_entry_id = le.leaderboard_entry_id
                where le.user_id = ?
                group by le.leaderboard_entry_id, le.ranking, le.score
                """.trimIndent(),
                { resultSet, _ ->
                    LeaderboardStandingDto(
                        position = resultSet.getInt("ranking"),
                        score = resultSet.getInt("score"),
                        goldMedals = resultSet.getInt("gold_medals"),
                        silverMedals = resultSet.getInt("silver_medals"),
                        bronzeMedals = resultSet.getInt("bronze_medals"),
                    )
                },
                userId,
            ).singleOrNull()
            ?: LeaderboardStandingDto(
                position = null,
                score = 0,
                goldMedals = 0,
                silverMedals = 0,
                bronzeMedals = 0,
            )

    fun getContestHistory(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): ContestHistoryPageDto {
        val totalEntries =
            jdbcTemplate.queryForObject(
                """
                select count(*)
                from t_participant p
                join t_contest c on c.contest_id = p.contest_id
                where p.user_id = ?
                  and c.contest_status = 'COMPLETED'
                """.trimIndent(),
                Long::class.java,
                userId,
            ) ?: 0L

        val entries =
            jdbcTemplate.query(
                """
                select
                    c.contest_id,
                    c.contest_name,
                    c.start_time,
                    c.end_time,
                    p.participant_rank,
                    p.total_value,
                    p.total_value - $STARTING_PORTFOLIO_VALUE as gain_loss,
                    round(
                        (((p.total_value - $STARTING_PORTFOLIO_VALUE) / $STARTING_PORTFOLIO_VALUE) * 100)::numeric,
                        2
                    ) as return_percentage
                from t_participant p
                join t_contest c on c.contest_id = p.contest_id
                where p.user_id = ?
                  and c.contest_status = 'COMPLETED'
                order by c.end_time desc, c.contest_id desc
                limit ? offset ?
                """.trimIndent(),
                { resultSet, _ ->
                    ContestPerformanceDto(
                        contestId = resultSet.getLong("contest_id"),
                        contestName = resultSet.getString("contest_name"),
                        startTime = resultSet.getObject("start_time", LocalDateTime::class.java),
                        endTime = resultSet.getObject("end_time", LocalDateTime::class.java),
                        rank =
                            requireNotNull(resultSet.getObject("participant_rank", Int::class.javaObjectType)) {
                                "Completed contest participant rank must not be null"
                            },
                        finalPortfolioValue = resultSet.getBigDecimal("total_value").scaled(),
                        gainLoss = resultSet.getBigDecimal("gain_loss").scaled(),
                        returnPercentage = resultSet.getBigDecimal("return_percentage").scaled(),
                    )
                },
                userId,
                pageSize,
                pageNumber.toLong() * pageSize,
            )

        return ContestHistoryPageDto(entries = entries, totalEntriesCount = totalEntries)
    }

    private fun BigDecimal.scaled(): BigDecimal = setScale(2, RoundingMode.HALF_UP)

    private companion object {
        const val STARTING_PORTFOLIO_VALUE = "20000.00"
    }
}

data class PublicUserIdentity(
    val userId: Long,
    val username: String,
    val fullName: String?,
    val country: String?,
)
