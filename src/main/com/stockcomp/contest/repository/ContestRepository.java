package com.stockcomp.contest.repository;

import com.stockcomp.contest.entity.Contest;
import com.stockcomp.contest.entity.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Contest findByContestNumber(Integer contestNumber);

    Contest findByContestStatus(ContestStatus contestStatus);

    Contest findByContestNumberAndContestStatus(Integer contestNumber, ContestStatus contestStatus);

    List<Contest> findAllByContestStatus(ContestStatus contestStatus);

    @Query("SELECT c FROM Contest c WHERE c.contestStatus IN ?1")
    List<Contest> findAllByContestStatusList(List<ContestStatus> contestStatusList);
}
