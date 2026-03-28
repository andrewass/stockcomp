# Observability Prometheus Loki Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 for Spring Boot metrics + repository evidence for Loki/Prometheus setup
- Primary Context7 library:
  - `/spring-projects/spring-boot/v4.0.3`

## Source Baseline

- Spring Boot Actuator Prometheus endpoint behavior and scrape configuration.
- Micrometer `MeterRegistry` patterns for custom metrics.
- Repository logging setup for Loki and Kubernetes ServiceMonitor configuration.

## Repository Evidence

- Actuator metrics exposure and Prometheus settings.
- Logback Loki appender configuration.
- Kubernetes observability manifests/scripts.

## Links

- https://docs.spring.io/spring-boot/reference/actuator/metrics.html
- https://docs.spring.io/spring-boot/reference/actuator/endpoints.html
- https://docs.spring.io/spring-boot/reference/actuator/prometheus.html
- https://grafana.com/docs/loki/latest/
