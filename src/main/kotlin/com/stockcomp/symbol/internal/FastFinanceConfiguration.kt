package com.stockcomp.symbol.internal

import io.netty.channel.ChannelOption
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.validation.annotation.Validated
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration

@ConfigurationProperties("fastfinance")
@Validated
data class FastFinanceProperties(
    val baseUrl: URI,
    val connectTimeout: Duration,
    val responseTimeout: Duration,
    val retry: RetryProperties,
) {
    init {
        require(baseUrl.scheme in SUPPORTED_SCHEMES && baseUrl.host != null) {
            "fastfinance.base-url must be an absolute HTTP(S) URL"
        }
        require(connectTimeout >= MIN_CONNECT_TIMEOUT && connectTimeout <= MAX_CONNECT_TIMEOUT) {
            "fastfinance.connect-timeout must be between 1 ms and ${MAX_CONNECT_TIMEOUT.toMillis()} ms"
        }
        require(responseTimeout.isPositive()) {
            "fastfinance.response-timeout must be positive"
        }
    }

    data class RetryProperties(
        val maxRetries: Long,
        val backoff: Duration,
    ) {
        init {
            require(maxRetries >= 0) { "fastfinance.retry.max-retries must not be negative" }
            require(backoff.isPositive()) { "fastfinance.retry.backoff must be positive" }
        }
    }

    private companion object {
        val SUPPORTED_SCHEMES = setOf("http", "https")
        val MIN_CONNECT_TIMEOUT: Duration = Duration.ofMillis(1)
        val MAX_CONNECT_TIMEOUT: Duration = Duration.ofMillis(Int.MAX_VALUE.toLong())
    }
}

@Configuration
class FastFinanceConfiguration {
    @Bean("fastfinanceWebClient")
    fun fastFinanceWebClient(properties: FastFinanceProperties): WebClient {
        val httpClient =
            HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.connectTimeout.toMillis().toInt())
                .responseTimeout(properties.responseTimeout)

        return WebClient
            .builder()
            .baseUrl(properties.baseUrl.toString())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES)
            }.build()
    }

    private companion object {
        const val MAX_IN_MEMORY_SIZE_BYTES = 20 * 1024 * 1024
    }
}

private fun Duration.isPositive(): Boolean = !isZero && !isNegative
