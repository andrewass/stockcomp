package com.stockcomp.schedule;

import com.stockcomp.consumer.StockConsumer;
import com.stockcomp.document.SymbolDocument;
import com.stockcomp.repository.document.InvestmentDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SymbolScheduler {

    private final StockConsumer stockConsumer;

    private final InvestmentDocumentRepository repository;

    private final Logger logger = LoggerFactory.getLogger(SymbolScheduler.class);

    public SymbolScheduler(StockConsumer stockConsumer, InvestmentDocumentRepository repository) {
        this.stockConsumer = stockConsumer;
        this.repository = repository;
    }

    @Scheduled(fixedRateString = "${schedule.rate}")
    public void populateSymbols() {
        logger.info("Populating symbols for ElasticSearch");
        var symbols = new ArrayList<SymbolDocument>();

        var stockExchanges = findStockExchanges();
        symbols.addAll( populateSymbolsFromStockExchanges(stockExchanges));

        updatePersistedSymbolDocuments(symbols);
    }


    private List<SymbolDocument> populateSymbolsFromStockExchanges(List<String> stockExchanges){
        var symbols = new ArrayList<SymbolDocument>();

        for (String exchange : stockExchanges){
            symbols.addAll(stockConsumer.findAllSymbolsForExchange(exchange));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return symbols;
    }

    private List<String> findStockExchanges() {
        var streamReader = new InputStreamReader(this.getClass().getResourceAsStream("/domain/stockexchanges.csv"));
        var bufferedReader = new BufferedReader(streamReader);
        var exchanges = new ArrayList<String>();

        try {
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                String[] data = row.split(",");
                exchanges.add(data[0]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("IOException reading stock exchanges " + e.getMessage());
        }
        return exchanges;
    }

    private void updatePersistedSymbolDocuments(ArrayList<SymbolDocument> symbols) {
        repository.deleteAll();
        repository.saveAll(symbols);
        logger.info("Number of SymbolDocument fetched and persisted : "+repository.count());
    }
}
