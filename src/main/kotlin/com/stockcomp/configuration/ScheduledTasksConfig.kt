package com.stockcomp.configuration

import jakarta.validation.constraints.Positive
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.validation.annotation.Validated

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@ConditionalOnProperty(name = ["scheduling.enabled"], havingValue = "true", matchIfMissing = true)
class ScheduledTasksConfig

@Validated
@ConfigurationProperties("scheduling.tasks.investment.maintain-investments")
data class InvestmentMaintenanceProperties(
    @field:Positive
    val maxParticipantsPerRun: Int = 1_000,
)

@Validated
@ConfigurationProperties("scheduling.tasks.investment-order.maintain-investment-orders")
data class InvestmentOrderMaintenanceProperties(
    @field:Positive
    val maxParticipantsPerRun: Int = 1_000,
)

@Validated
@ConfigurationProperties("scheduling.tasks.leaderboard.create-jobs")
data class LeaderboardJobCreationProperties(
    @field:Positive
    val maxContestsPerRun: Int = 1_000,
)
