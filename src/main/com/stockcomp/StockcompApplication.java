package com.stockcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com/stockcomp/repository/jpa")
@EnableElasticsearchRepositories(basePackages = "com/stockcomp/repository/document")
public class StockcompApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockcompApplication.class, args);
    }
}
