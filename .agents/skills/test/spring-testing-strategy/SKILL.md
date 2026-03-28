---
name: spring-testing-strategy
description: Route Spring Boot testing tasks to the right scope (unit, slice, security, module, integration). Use to choose test scope and skill composition; specialized testing skills own concrete framework assertions.
---

# Spring Testing Strategy

Use this skill to pick the smallest effective test scope first, then expand only when needed.

## Scope and Precedence

- This is a routing skill, not a detailed assertion catalog.
- When a specialized testing skill is active, that skill is authoritative for concrete assertions and tooling.
- Use this skill to choose scope; use specialized skills to define exact tests.

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

3. Define minimum assertion categories
- Success path.
- Failure path.
- Security path (allow + deny) when endpoint/service is protected.
- Let specialized skills define exact framework-level assertions.

4. Keep runtime budget under control
- Avoid loading full context for slice-level concerns.
- Keep integration fixtures small and deterministic.

5. Verify quality gate
- Tests prove behavior, not implementation details.
- Test names describe business behavior.
- No duplicate coverage across scopes unless intentional risk mitigation.

## Source Baseline

See [references/testing-strategy-reference.md](references/testing-strategy-reference.md) for the documentation baseline and refresh prompts.
