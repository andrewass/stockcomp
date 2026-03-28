---
name: flyway-postgres-migrations
description: Design and review Flyway SQL migrations for PostgreSQL in Spring Boot projects. Use for schema evolution, migration naming/versioning, repeatable migrations, validation, and safe rollout patterns.
---

# Flyway PostgreSQL Migrations

Use this skill for reliable database schema evolution with Flyway.

## Workflow

1. Classify the migration
- Versioned migration for one-time schema/data changes.
- Repeatable migration for views/functions/procedures that should rerun on checksum change.

2. Apply naming and ordering
- Use versioned naming `V<version>__<description>.sql`.
- Use repeatable naming `R__<description>.sql`.
- Keep versioning monotonic and deterministic.

3. Prefer safe, forward-only SQL
- Use additive changes first (new columns/tables/indexes) before destructive cleanup.
- Avoid editing already applied versioned scripts.
- Split risky changes into multiple deploy-safe steps when needed.

4. Keep data migrations controlled
- Make backfills explicit and bounded.
- Ensure SQL is deterministic and idempotent where feasible.

5. Validate before applying
- Run Flyway validation/info in CI and local verification before merge.
- Ensure migration history stays consistent across environments.

## Quality Gate

- Migration name/versioning follows convention.
- No destructive or non-backward-compatible step without staged rollout.
- Script is reviewed for lock impact and execution time.
- CI includes migration validation.

## Source Baseline

See [references/flyway-postgres-migrations-reference.md](references/flyway-postgres-migrations-reference.md).
