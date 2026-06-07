package com.stockcomp.symbol.internal

import com.stockcomp.symbol.CurrentPriceSymbolDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URI
import java.time.Duration

@Component("fastfinance.quote.consumer")
class FastFinanceConsumer(
    private val webClient: WebClient,
    @param:Value("\${fastfinance.base.url}") private val baseUrl: String,
    @param:Value("\${fastfinance.request-timeout:5s}") private val requestTimeout: Duration = Duration.ofSeconds(5),
    @param:Value("\${fastfinance.retry.max-attempts:2}") private val retryMaxAttempts: Long = 2,
    @param:Value("\${fastfinance.retry.backoff:200ms}") private val retryBackoff: Duration = Duration.ofMillis(200),
) : QuoteConsumer {
    override fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto =
        requireNotNull(
            webClient
                .get()
                .uri(URI("$baseUrl/price/current-price/$symbol"))
                .retrieve()
                .bodyToMono<CurrentPriceSymbolResponse>()
                .withFastFinanceHandling("current price request for symbol=$symbol")
                .block()
                ?.toCurrentPriceSymbolDto(),
        ) { throw FastFinanceClientException("FastFinance returned empty current price for symbol=$symbol") }

    override fun getCurrentPriceTrendingSymbols(symbols: List<String>): List<CurrentPriceSymbolDto> =
        requireNotNull(
            webClient
                .post()
                .uri(URI("$baseUrl/price/symbols"))
                .bodyValue(TrendingSymbolsRequest(symbols))
                .retrieve()
                .bodyToMono<List<CurrentPriceSymbolResponse>>()
                .withFastFinanceHandling("trending symbols request for symbols=$symbols")
                .block(),
        ) { throw FastFinanceClientException("FastFinance returned empty trending symbols response for symbols=$symbols") }
            .map { it.toCurrentPriceSymbolDto() }

    private fun <T : Any> Mono<T>.withFastFinanceHandling(operation: String): Mono<T> =
        timeout(requestTimeout)
            .retryWhen(Retry.backoff(retryMaxAttempts, retryBackoff))
            .onErrorMap { cause ->
                cause as? FastFinanceClientException ?: FastFinanceClientException("FastFinance $operation failed", cause)
            }

    private data class TrendingSymbolsRequest(
        val symbols: List<String>,
    )

    private data class CurrentPriceSymbolResponse(
        val symbol: String,
        val companyName: String,
        val currentPrice: BigDecimal,
        val previousClose: BigDecimal,
        val currency: String,
    )

    private fun CurrentPriceSymbolResponse.toCurrentPriceSymbolDto(): CurrentPriceSymbolDto =
        CurrentPriceSymbolDto(
            symbol = symbol,
            companyName = companyName,
            currentPrice = currentPrice,
            previousClose = previousClose,
            currency = currency,
            percentageChange =
                currentPrice
                    .subtract(previousClose)
                    .multiply(BigDecimal("100"))
                    .divide(previousClose, 10, RoundingMode.HALF_UP),
            usdPrice = currentPrice,
        )
}
