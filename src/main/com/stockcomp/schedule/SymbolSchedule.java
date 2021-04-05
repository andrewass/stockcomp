package com.stockcomp.schedule;

import com.stockcomp.consumer.StockConsumer;
import com.stockcomp.repository.InvestmentDocumentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SymbolSchedule {

    private final StockConsumer stockConsumer;
    private final InvestmentDocumentRepository repository;

    public SymbolSchedule(StockConsumer stockConsumer, InvestmentDocumentRepository repository) {
        this.stockConsumer = stockConsumer;
        this.repository = repository;
    }

    @Scheduled(fixedRateString = "${schedule.rate}")
    public void populateSymbols(){

    }
}
