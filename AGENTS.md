# Agents Guide

This file gives short, practical instructions for working in this repository.

## Project Snapshot
- Domain: Stock competition app
- Stack: Spring Boot 4 + Kotlin, JPA/Hibernate, PostgreSQL, Flyway
- Build tool: Maven Wrapper

## Repository Layout
- `C:\Users\andreas\Documents\stockcomp\src\main\kotlin` application code
- `C:\Users\andreas\Documents\stockcomp\src\test\kotlin` tests
- `C:\Users\andreas\Documents\stockcomp\k8s` Kubernetes manifests
- `C:\Users\andreas\Documents\stockcomp\docker-compose.yml` local services

## Common Commands
- Build: `./mvnw -DskipTests package`
- Unit tests: `./mvnw test`
- Integration tests (if configured): `./mvnw -DskipITs=false verify`
- Run app: `./mvnw spring-boot:run`
- Ktlint check (verify only): `./mvnw exec:exec@ktlint-check`
- Ktlint format (auto-fix): `./mvnw exec:exec@ktlint-format`

## Conventions
- Prefer Kotlin data classes for DTOs and use immutable collections where possible.
- Keep Spring beans small and single-purpose; favor constructor injection.
- Use Flyway migrations for schema changes.
- For persistence work, keep entity mappings lean and avoid N+1 queries.

## Quality & Checks
- Kotlin formatting is enforced in CI via ktlint. Run the commands above before opening a PR.
- Add or update tests for behavioral changes.

## Working Notes
- If you touch database code, scan for transaction boundaries and lazy-loading pitfalls.
- When adding endpoints, consider both WebMVC and WebFlux usage; follow existing patterns.

## Skills
If a task matches a listed skill in `.agents/skills`, use that skill’s instructions for this turn.
