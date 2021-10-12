package com.stockcomp.repository;


import com.stockcomp.domain.leaderboard.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {

    List<LeaderboardEntry> findAllByOrderByRankingAsc();
}
