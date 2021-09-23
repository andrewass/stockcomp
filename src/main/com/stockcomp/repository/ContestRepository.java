package com.stockcomp.repository;

import com.stockcomp.domain.contest.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Contest findByContestNumber(Integer contestNumber);

    Contest findByContestNumberAndRunningIsTrue(Integer contestNumber);

    Contest findByContestNumberAndCompletedIsFalseAndRunningIsFalse(Integer contestNumber);

    Contest findByContestNumberAndCompleted(Integer contestNumber, Boolean completed);

    List<Contest> findAllByCompleted(Boolean completed);
}
