package com.stockcomp.kafka.consumer;

import com.stockcomp.request.InvestmentTransactionRequest;
import com.stockcomp.service.InvestmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContestConsumer {

    private final InvestmentService investmentService;
    private final Logger logger = LoggerFactory.getLogger(ContestConsumer.class);

    ContestConsumer(InvestmentService investmentService){
        this.investmentService = investmentService;
    }

    @KafkaListener(topics = "buy-investment", groupId = "group-id")
    public void consumeBuyInvestment(InvestmentTransactionRequest request) {
        logger.info("Consumed buy-investment : " + request);
        investmentService.buyInvestment(request);
    }

    @KafkaListener(topics = "sell-investment", groupId = "group-id")
    public void consumeSellInvestment(InvestmentTransactionRequest request){
        logger.info("Consumed sell-investment : "+ request);
        investmentService.sellInvestment(request);
    }
}