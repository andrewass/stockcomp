---
name: observability-prometheus-loki
description: Implement observability foundations in Spring Boot services using Actuator, Micrometer/Prometheus metrics, and structured Loki logging. Use for metrics exposure, scrape integration, log schema, and operational diagnostics.
---

# Observability Prometheus Loki

Use this skill for practical service observability in Kubernetes environments.

## Workflow

1. Expose operational metrics
- Enable Actuator metrics endpoints deliberately.
- Ensure `/actuator/prometheus` exposure aligns with environment policy.

2. Instrument meaningful signals
- Register domain metrics using `MeterRegistry` where business outcomes matter.
- Track throughput, latency, errors, and queue/backlog signals.

3. Standardize structured logs
- Use stable fields (service, level, request ID, class/thread context).
- Avoid leaking sensitive payloads.

4. Connect platform scraping/log shipping
- Configure Prometheus scrape targets or ServiceMonitors.
- Ensure Loki pipeline labels are stable and query-friendly.

## Quality Gate

- Metrics endpoint works and is scraped in target environment.
- Logs are structured and searchable with stable labels.
- Key operations are covered by metrics and logs, not only infrastructure-level metrics.

## Source Baseline

See [references/observability-prometheus-loki-reference.md](references/observability-prometheus-loki-reference.md).
