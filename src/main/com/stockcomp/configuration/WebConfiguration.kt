package com.stockcomp.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebConfiguration {

    @Bean
    fun webClient(): WebClient =
        WebClient.builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer
                    .defaultCodecs()
                    .maxInMemorySize(20 * 1024 * 1024)
            }
            .build()
}