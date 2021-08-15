package com.stockcomp.repository;

import com.stockcomp.domain.contest.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Optional<Contest> findContestByContestNumber(Integer contestNumber);

    Optional<Contest> findContestByContestNumberAndRunningIsTrue(Integer contestNumber);

    Optional<Contest> findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(Integer contestNumber);

    Contest findContestByContestNumberAndCompletedIsFalseOrRunningIsTrue(Integer contestNumber);

    List<Contest> findAllByCompletedIsFalseOrRunningIsTrue();

    List<Contest> findAllByRunningIsTrue();
}
