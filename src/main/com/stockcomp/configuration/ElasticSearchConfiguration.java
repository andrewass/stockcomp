package com.stockcomp.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticSearchConfiguration extends ElasticsearchConfigurationSupport {

    @Bean
    public RestHighLevelClient client(){
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("es01:9200")
                .withConnectTimeout(1000*60)
                .withSocketTimeout(1000*60)
                .build();

        return RestClients.create(configuration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(){
        return new ElasticsearchRestTemplate(client());
    }
}
