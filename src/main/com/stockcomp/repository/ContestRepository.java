package com.stockcomp.repository;

import com.stockcomp.domain.contest.Contest;
import com.stockcomp.domain.contest.enums.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Contest findByContestNumber(Integer contestNumber);

    Contest findByContestStatus(ContestStatus contestStatus);

    Contest findByContestNumberAndContestStatus(Integer contestNumber, ContestStatus contestStatus);

    List<Contest> findAllByContestStatus(ContestStatus contestStatus);
}
