package com.stockcomp.repository;

import com.stockcomp.entity.InvestmentUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentUnitRepository extends JpaRepository<InvestmentUnit, Long> {

    InvestmentUnit findBySymbol(String symbol);
}
