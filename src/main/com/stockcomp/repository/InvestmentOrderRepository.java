package com.stockcomp.repository;

import com.stockcomp.domain.contest.InvestmentOrder;
import com.stockcomp.domain.contest.enums.ContestStatus;
import com.stockcomp.domain.contest.enums.OrderStatus;
import com.stockcomp.domain.contest.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> {

    List<InvestmentOrder> findAllByOrderStatus(OrderStatus orderStatus);

    List<InvestmentOrder> findAllByParticipantAndOrderStatusIn(Participant participant, List<OrderStatus> orderStatus);

    List<InvestmentOrder> findAllByParticipantAndSymbolAndOrderStatusIn(
            Participant participant, String symbol, List<OrderStatus> orderStatus
    );

    @Query("SELECT io FROM InvestmentOrder io join io.participant p join p.contest c " +
            "where io.orderStatus = ?1 and c.contestStatus  = ?2")
    List<InvestmentOrder> findAllByOrderAndContestStatus(OrderStatus orderStatus, ContestStatus contestStatus);
}
