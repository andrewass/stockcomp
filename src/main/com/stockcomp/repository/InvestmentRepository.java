package com.stockcomp.repository;

import com.stockcomp.domain.contest.Investment;
import com.stockcomp.domain.contest.Participant;
import com.stockcomp.domain.contest.enums.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    Investment findBySymbolAndParticipant(String symbol, Participant participant);

    List<Investment> findAllByParticipant(Participant participant);

    @Query("SELECT i FROM Investment i join i.participant p join p.contest c where c.contestStatus  = ?1")
    List<Investment> findAllInvestmentsByContestStatus(ContestStatus contestStatus);
}
