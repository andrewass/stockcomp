package com.stockcomp.leaderboard.repository;


import com.stockcomp.leaderboard.entity.LeaderboardEntry;
import com.stockcomp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {

    List<LeaderboardEntry> findAllByOrderByRanking();

    List<LeaderboardEntry> findAllByOrderByScore();

    LeaderboardEntry findByUser(User user);
}
