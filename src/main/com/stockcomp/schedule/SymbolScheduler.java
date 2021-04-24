package com.stockcomp.schedule;

import com.stockcomp.consumer.StockConsumer;
import com.stockcomp.document.Exchange;
import com.stockcomp.document.SymbolDocument;
import com.stockcomp.repository.document.SymbolDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SymbolScheduler {

    private final StockConsumer stockConsumer;

    private final SymbolDocumentRepository repository;

    private final Logger logger = LoggerFactory.getLogger(SymbolScheduler.class);

    public SymbolScheduler(StockConsumer stockConsumer, SymbolDocumentRepository repository) {
        this.stockConsumer = stockConsumer;
        this.repository = repository;
    }

    @Scheduled(fixedRateString = "${schedule.rate}")
    public void populateSymbols() {
        logger.info("Populating symbols for ElasticSearch");
        var stockExchanges = findStockExchanges();
        updatePersistedSymbolDocuments(fetchSymbolsFromStockExchanges(stockExchanges));
    }

    private List<SymbolDocument> fetchSymbolsFromStockExchanges(List<Exchange> stockExchanges) {
        var symbols = new ArrayList<SymbolDocument>();
        for (Exchange exchange : stockExchanges) {
            var exchangeSymbols = stockConsumer.findAllSymbolsForExchange(exchange.getExchangeCode());
            for (SymbolDocument symbol : exchangeSymbols) {
                symbol.setExchange(exchange);
                symbols.add(symbol);
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return symbols;
    }

    private List<Exchange> findStockExchanges() {
        var streamReader = new InputStreamReader(this.getClass().getResourceAsStream("/domain/stockexchanges.csv"));
        var bufferedReader = new BufferedReader(streamReader);
        var exchanges = new ArrayList<Exchange>();

        try {
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                String[] data = row.split(",");
                exchanges.add(new Exchange(data[0], data[1]));
            }
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("IOException reading stock exchanges " + e.getMessage());
        }
        return exchanges;
    }

    private void updatePersistedSymbolDocuments(List<SymbolDocument> symbols) {
        repository.deleteAll();
        repository.saveAll(symbols);
        logger.info("Number of SymbolDocument fetched and persisted : " + repository.count());
    }
}
