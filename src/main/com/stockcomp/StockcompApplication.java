package com.stockcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class StockcompApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockcompApplication.class, args);
    }
}
