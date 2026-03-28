---
name: spring-webclient-outbound-http
description: Design and review outbound HTTP integrations with Spring WebClient in Spring Boot applications. Use for client configuration, timeouts, error mapping, resilience patterns, and response model mapping.
---

# Spring WebClient Outbound HTTP

Use this skill for resilient outbound API integrations.

## Workflow

1. Define client contract
- Define base URL, auth model, expected status codes, and timeout expectations.
- Keep DTO mapping and transport concerns explicit.

2. Configure WebClient intentionally
- Set connect/read/response timeout behavior.
- Use a dedicated client configuration per integration concern when needed.

3. Handle failures explicitly
- Map non-2xx responses to typed domain errors.
- Add retry/backoff only for transient, idempotent operations.
- Avoid hidden retries for non-idempotent requests.

4. Bridge reactive/blocking deliberately
- In MVC apps, blocking at service edge may be acceptable; avoid mixed async behavior deeper in call chains.
- Keep blocking boundaries explicit and consistent.

5. Add observability
- Log request context safely (without sensitive data).
- Capture latency/error metrics for downstream calls.

## Quality Gate

- Timeouts are configured and justified.
- Error mapping is explicit and tested.
- Retry behavior is deliberate, bounded, and idempotency-aware.
- Client behavior is observable in logs/metrics.

## Source Baseline

See [references/spring-webclient-outbound-http-reference.md](references/spring-webclient-outbound-http-reference.md).
