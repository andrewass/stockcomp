package com.stockcomp.repository;

import com.stockcomp.domain.contest.Investment;
import com.stockcomp.domain.contest.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    Investment findBySymbolAndParticipant(String symbol, Participant participant);
}
