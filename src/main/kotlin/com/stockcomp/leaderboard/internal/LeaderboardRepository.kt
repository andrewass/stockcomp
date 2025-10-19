package com.stockcomp.leaderboard.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeaderboardRepository : JpaRepository<Leaderboard, Long>