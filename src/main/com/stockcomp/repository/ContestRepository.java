package com.stockcomp.repository;

import com.stockcomp.entity.contest.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    Optional<Contest> findByInPreStartModeIsTrue();

    Optional<Contest> findByInRunningModeIsTrue();
}
