# Spring WebClient Outbound HTTP Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring Boot docs
- Primary Context7 library:
  - `/spring-projects/spring-boot/v4.0.3`

## Source Baseline

- Spring Boot HTTP client configuration patterns for WebClient.
- Reactor Netty configuration guidance for connection and read timeout behavior.

## Extracted Guidance

1. Configure timeouts explicitly at client level.
2. Keep WebClient setup centralized and reusable.
3. Treat downstream error mapping as part of domain behavior, not transport leakage.
4. Add latency/error observability around outbound calls.

## Links

- https://docs.spring.io/spring-boot/reference/io/rest-client.html
- https://docs.spring.io/spring-boot/reference/how-to/http-clients.html
