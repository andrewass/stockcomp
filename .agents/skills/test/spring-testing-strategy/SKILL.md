---
name: spring-testing-strategy
description: Route Spring Boot testing tasks to the right test type and scope. Use when deciding between unit, slice, module, security, and full integration tests while keeping suites fast and reliable.
---

# Spring Testing Strategy

Use this skill to pick the smallest effective test scope first, then expand only when needed.

## Workflow

1. Classify the change
- Controller contract and serialization: use `spring-webmvc-testing`.
- Repository/query behavior: use `spring-data-jpa-testing`.
- Authentication/authorization behavior: use `spring-security-testing`.
- End-to-end DB + HTTP flow: use `spring-integration-testcontainers`.
- Module boundaries/events in modular monoliths: use `spring-modulith-testing`.

2. Choose one primary test scope
- Prefer unit/slice tests first for speed.
- Add integration tests only for cross-layer behavior or wiring risks.

3. Define minimum assertions
- Success path.
- Failure path.
- Security path (allow + deny) when endpoint/service is protected.

4. Keep runtime budget under control
- Avoid loading full context for slice-level concerns.
- Keep integration fixtures small and deterministic.

5. Verify quality gate
- Tests prove behavior, not implementation details.
- Test names describe business behavior.
- No duplicate coverage across scopes unless intentional risk mitigation.

## Source Baseline

See [references/testing-strategy-reference.md](references/testing-strategy-reference.md) for the documentation baseline and refresh prompts.
