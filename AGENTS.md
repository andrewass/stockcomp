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

## Dependency & import hygiene
- When introducing new framework annotations/APIs/imports, verify required dependencies are present in `pom.xml` (for example validation annotations require `spring-boot-starter-validation`).
- If a change introduces unresolved references due to missing dependencies, update the build file as part of the same change.
- Remove unused imports in all touched files before finishing.

## Commit conventions
- Commit messages must start with a capital letter.

## Working Notes
- If you touch database code, scan for transaction boundaries and lazy-loading pitfalls.
- When adding endpoints, consider both WebMVC and WebFlux usage; follow existing patterns.

## Modulith architecture
- This application is organized as a Spring Modulith modular monolith.
- Respect module boundaries: avoid introducing direct dependencies on internal packages from other modules.
- Prefer communication through existing exposed services/contracts and established module interaction patterns.
- When architectural/module boundaries change, update and verify modulith tests (for example `ApplicationModules.verify()` coverage).
- Keep module responsibilities cohesive; do not move business logic across modules without explicit architectural intent.

## Skills
- Skills under `.agents/skills/**` should be repo-agnostic by default so they can be reused across projects.
- Place reusable skills in `.agents/skills/**` as the default location.
- Do not hardcode repository names, repo-specific paths, or project-only assumptions in reusable skills.
- If project-specific behavior is needed, keep it clearly marked as optional project overlay guidance.
- If `.agents/skills/**` is not writable, stop and ask the user how to proceed before creating skills in any alternate directory.

## Skill folder grouping
- Group skills by domain under `.agents/skills/**` to keep the root tidy.
- Preferred groups:
  - `core/` language/framework fundamentals
  - `api/` controller and API contract skills
  - `data/` persistence and migrations
  - `security/` authn/authz and JWT handling
  - `integration/` outbound clients and external APIs
  - `runtime/` scheduling/background processing
  - `ops/` observability and platform operations
  - `test/` testing strategy and specialized testing skills

## Skill update guardrails
- Treat every skill listed in `skills-lock.json` as read-only.
- When asked to update skills, only modify skills not listed in `skills-lock.json`.
- If a requested skill is listed in `skills-lock.json`, do not edit it unless the user explicitly says `override lock for <skill-name>`.
- Do not modify `skills-lock.json` unless explicitly requested.
- If lock status is unclear, stop and ask before editing.

## Skill routing
- If the user says `use relevant skills`, automatically select and apply all matching skills from `.agents/skills/**`.
- If the user names a skill explicitly (for example `$kotlin-springboot`), always include it.
- Activate `kotlin-springboot` for Spring Boot + Kotlin architecture, idiomatic Kotlin patterns, bean/service design, and framework integration work.
- Activate `jpa-patterns` for JPA/Hibernate entity mapping, relationships, query optimization, transaction boundaries, auditing, indexing, pagination, and pooling.
- Activate `spring-security` for authentication/authorization config, SecurityFilterChain rules, method security, OAuth2 resource server/client setup, and security test coverage.
- Activate `flyway-postgres-migrations` for Flyway SQL migration design, versioning, repeatable migrations, and safe rollout patterns.
- Activate `spring-scheduling-shedlock` for `@Scheduled` + `@SchedulerLock` behavior, lock strategy, idempotency, and transactional job boundaries.
- Activate `spring-webclient-outbound-http` for outbound HTTP client design with `WebClient`, timeout/retry/error mapping, and downstream resilience.
- Activate `observability-prometheus-loki` for Actuator/Micrometer metrics exposure, Prometheus scraping, structured logging, and Loki integration.
- Activate `spring-jwt-claims-resolution` for JWT issuer/audience validation, claim-to-authority mapping, and request-context user resolution patterns.
- For mixed tasks, combine all relevant skills rather than choosing only one.
- Use this priority when guidance conflicts: correctness/security (`spring-security`, `kotlin-springboot`) before data integrity/performance (`jpa-patterns`) before style/ergonomics (`kotlin-springboot`).

## Skill conflict resolution
- Overlap between skills is allowed when it improves standalone usability.
- For overlapping topics, prefer the most specialized active skill for concrete implementation details.
- Treat strategy/router skills as scope selectors, not as authoritative sources for low-level framework assertions.
- For security tasks: `spring-security` is authoritative for architecture/configuration, while `spring-security-testing` is authoritative for test mechanics.
