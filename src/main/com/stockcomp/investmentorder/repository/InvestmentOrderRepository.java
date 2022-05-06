package com.stockcomp.investmentorder.repository;

import com.stockcomp.domain.contest.Contest;
import com.stockcomp.investmentorder.domain.InvestmentOrder;
import com.stockcomp.domain.contest.enums.ContestStatus;
import com.stockcomp.domain.contest.enums.OrderStatus;
import com.stockcomp.domain.contest.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> {

    List<InvestmentOrder> findAllByParticipantAndOrderStatusIn(Participant participant, List<OrderStatus> orderStatus);

    List<InvestmentOrder> findAllByParticipantAndSymbolAndOrderStatusIn(
            Participant participant, String symbol, List<OrderStatus> orderStatus
    );

    @Query("select io from InvestmentOrder io join io.participant p join p.contest c " +
            "where io.orderStatus = ?1 and c.contestStatus  = ?2")
    List<InvestmentOrder> findAllByOrderAndContestStatus(OrderStatus orderStatus, ContestStatus contestStatus);


    @Query("select io from InvestmentOrder io join io.participant p join p.contest c " +
            "where c = ?1 and io.orderStatus = ?2")
    List<InvestmentOrder> findAllByContestAndOrderStatus(Contest contest, OrderStatus orderStatus);
}
