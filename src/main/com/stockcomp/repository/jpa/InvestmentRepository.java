package com.stockcomp.repository.jpa;

import com.stockcomp.domain.contest.Investment;
import com.stockcomp.domain.contest.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    Investment findBySymbolAndPortfolio(String symbol, Portfolio portfolio);
}
