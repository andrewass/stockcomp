package com.stockcomp.repository;

import com.stockcomp.domain.contest.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Contest findContestByContestNumber(Integer contestNumber);

    Contest findContestByContestNumberAndRunningIsTrue(Integer contestNumber);

    Contest findContestByContestNumberAndCompletedIsFalseAndRunningIsFalse(Integer contestNumber);

    Contest findContestByContestNumberAndCompleted(Integer contestNumber, Boolean completed);

    List<Contest> findAllByCompleted(Boolean completed);
}
