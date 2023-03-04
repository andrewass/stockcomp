package com.stockcomp.leaderboard.repository

import com.stockcomp.leaderboard.entity.Medal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MedalRepository : JpaRepository<Medal, Long>