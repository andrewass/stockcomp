package com.stockcomp.contest.repository;

import com.stockcomp.contest.entity.Contest;
import com.stockcomp.contest.entity.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Contest findByContestNumber(Integer contestNumber);

    Contest findByContestNumberAndContestStatus(Integer contestNumber, ContestStatus contestStatus);

    List<Contest> findAllByContestStatusIn(List<ContestStatus> contestStatusList);

    void deleteByContestNumber(Integer contestNumber);
}
