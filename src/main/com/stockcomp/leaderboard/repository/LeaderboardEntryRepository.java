package com.stockcomp.leaderboard.repository;


import com.stockcomp.leaderboard.domain.LeaderboardEntry;
import com.stockcomp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {

    List<LeaderboardEntry> findAllByOrderByRanking();

    List<LeaderboardEntry> findAllByOrderByScore();

    LeaderboardEntry findByUser(User user);
}
