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
- Skills under `.agents/skills/**` should be repo-agnostic by default so they can be reused across projects.
- Place reusable skills in `.agents/skills/**` as the default location.
- Do not hardcode repository names, repo-specific paths, or project-only assumptions in reusable skills.
- If project-specific behavior is needed, keep it clearly marked as optional project overlay guidance.
- If `.agents/skills/**` is not writable, stop and ask the user how to proceed before creating skills in any alternate directory.

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
- For mixed tasks, combine all relevant skills rather than choosing only one.
- Use this priority when guidance conflicts: correctness/security (`spring-security`, `kotlin-springboot`) before data integrity/performance (`jpa-patterns`) before style/ergonomics (`kotlin-springboot`).

## Skill conflict resolution
- Overlap between skills is allowed when it improves standalone usability.
- For overlapping topics, prefer the most specialized active skill for concrete implementation details.
- Treat strategy/router skills as scope selectors, not as authoritative sources for low-level framework assertions.
- For security tasks: `spring-security` is authoritative for architecture/configuration, while `spring-security-testing` is authoritative for test mechanics.
