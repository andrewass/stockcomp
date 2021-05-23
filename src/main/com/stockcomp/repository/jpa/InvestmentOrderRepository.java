package com.stockcomp.repository.jpa;

import com.stockcomp.domain.contest.InvestmentOrder;
import com.stockcomp.domain.contest.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> {

    List<InvestmentOrder> findAllByOrderStatus(OrderStatus orderStatus);
}
