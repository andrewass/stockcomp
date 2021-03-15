package com.stockcomp.kafka.producer;

import com.stockcomp.request.InvestmentTransactionRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String SELL_INVESTMENT = "sell-investment";
    private final String BUY_INVESTMENT = "buy-investment";

    public ContestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void buyInvestment(InvestmentTransactionRequest request) {
        kafkaTemplate.send(BUY_INVESTMENT, request);
    }

    public void sellInvestment(InvestmentTransactionRequest request) {
        kafkaTemplate.send(SELL_INVESTMENT, request);
    }
}
