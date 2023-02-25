package com.stockcomp.investmentorder.repository;

import com.stockcomp.contest.entity.Contest;
import com.stockcomp.investmentorder.entity.InvestmentOrder;
import com.stockcomp.contest.entity.ContestStatus;
import com.stockcomp.investmentorder.entity.OrderStatus;
import com.stockcomp.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> {

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
