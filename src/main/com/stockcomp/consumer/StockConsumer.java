package com.stockcomp.consumer;

import com.stockcomp.entity.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class StockConsumer {

    private final WebClient.Builder webClientBuilder;

    public StockConsumer(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public List<Exchange> getExchanges(String exchangeUrl){
        return Collections.emptyList();
    }
}
