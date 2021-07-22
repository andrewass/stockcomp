package com.stockcomp.repository;

import com.stockcomp.domain.contest.InvestmentOrder;
import com.stockcomp.domain.contest.OrderStatus;
import com.stockcomp.domain.contest.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> {

    List<InvestmentOrder> findAllByOrderStatus(OrderStatus orderStatus);

    List<InvestmentOrder> findAllByParticipantAndOrderStatusIn(Participant participant, List<OrderStatus> orderStatus);

    List<InvestmentOrder> findAllByParticipantAndSymbolAndOrderStatusIn(
            Participant participant, String symbol, List<OrderStatus> orderStatus
    );
}
