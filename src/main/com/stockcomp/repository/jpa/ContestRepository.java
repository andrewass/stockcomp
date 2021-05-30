package com.stockcomp.repository.jpa;

import com.stockcomp.domain.contest.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Optional<Contest> findContestByContestNumber(Integer contestNumber);

    Optional<Contest> findContestByContestNumberAndInRunningModeIsTrue(Integer contestNumber);

    Optional<Contest> findContestByContestNumberAndInPreStartModeIsTrue(Integer contestNumber);

    Contest findContestByContestNumberAndInPreStartModeIsTrueOrInRunningModeIsTrue(Integer contestNumber);

    List<Contest> findAllByInRunningModeIsTrueOrInPreStartModeIsTrue();
}
