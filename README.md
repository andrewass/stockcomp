# StockComp

StockComp is a Spring Boot backend for stock competitions. It manages users, contests, participants, orders/investments, and a shared leaderboard.

## Tech Stack

- Kotlin + Spring Boot `4.0.4`
- Java `25`
- Spring WebMVC, Spring Security (OAuth2 Resource Server), Spring Data JPA
- PostgreSQL + Flyway migrations
- ShedLock for distributed scheduler locking
- Micrometer/Actuator + Prometheus + Loki (non-local profile)
- Maven (Maven Wrapper is preferred: `mvnw` / `mvnw.cmd`)

## Main Modules

- `user`: user profile, JWT subject mapping, role resolution
- `contest`: create/update/delete contests and status transitions
- `participant`: signup, history, investment and order views
- `symbol`: market data through FastFinance (`fastfinance.base.url`)
- `leaderboard`: leaderboard entries and async completion jobs
- `configuration` / `token` / `exception`: security, argument resolvers, error handling

The project verifies module boundaries with Spring Modulith (`ApplicationModules.verify()` test).

## Local Setup

### Prerequisites

- JDK 25
- Docker (for local PostgreSQL and Testcontainers-based tests)

Command notation used below:

- `mvnw` in examples means:
- use `./mvnw` on Unix/macOS/WSL
- use `mvnw.cmd` on Windows

### 1) Start PostgreSQL

```bash
docker compose up -d
```

### 2) Run the app with local profile

The local profile points datasource to `localhost:5432` and JWT/JWK endpoints to `localhost:3050`.

```bash
mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The app starts on `http://localhost:8080`.

## Build, Test, and Formatting

Maven Wrapper is preferred for consistency across environments.

```bash
mvnw -DskipTests package
mvnw test
mvnw -DskipITs=false verify
mvnw exec:exec@ktlint-check
mvnw exec:exec@ktlint-format
```

Recommended verification flow after code changes:

```bash
mvnw clean verify -DskipITs=false
mvnw exec:exec@ktlint-format
```

Note: Integration tests use Testcontainers (PostgreSQL), so a compatible container runtime must be available.

## Configuration

Important properties (`application.properties` / `application-local.properties`):

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `fastfinance.base.url`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`
- `spring.security.oauth2.resourceserver.jwt.audience`
- `scheduling.enabled` (default `true`)

## Security and Authentication

- All endpoints require authentication except a small list of permitted paths (including `/actuator/*`).
- JWTs are validated with issuer + audience validators.
- `@TokenData` resolves internal `userId` from claims.
- Preferred claim: `email`.
- Fallback claim: `sub`.
- Authorities are resolved from internal user roles (`ROLE_<UserRole>`) and used by `@PreAuthorize`.

## API Overview

Main endpoints (base URL `http://localhost:8080`):

### Users

- `GET /users/details?userId=...`
- `GET /users/admin`
- `GET /users/sorted?pageNumber=...&pageSize=...` (ADMIN)
- `POST /users/create` (ADMIN)
- `PATCH /users/update` (ADMIN)

### Contests

- `GET /contests/all?pageNumber=...&pageSize=...`
- `GET /contests/exists-active`
- `GET /contests/active`
- `GET /contests/{contestId}`
- `POST /contests/create` (ADMIN)
- `PATCH /contests/update` (ADMIN)
- `DELETE /contests/{contestId}` (ADMIN)

### Participants / Investments / Orders

- `POST /participants/sign-up`
- `GET /participants/registered`
- `GET /participants/unregistered`
- `GET /participants/detailed/symbol/{symbol}`
- `GET /participants/detailed/contest/{contestId}`
- `GET /participants/sorted?contestId=...&pageNumber=...&pageSize=...`
- `GET /participants/history?username=...`
- `GET /participants/investments/all?contestId=...`
- `GET /participants/investments?contestId=...&symbol=...`
- `POST /participants/investmentorders/order`
- `DELETE /participants/investmentorders/delete?orderId=...&contestId=...`
- `GET /participants/investmentorders/all-active?contestId=...`
- `GET /participants/investmentorders/all-completed?contestId=...`
- `GET /participants/investmentorders/symbol-active?contestId=...&symbol=...`
- `GET /participants/investmentorders/symbol-completed?contestId=...&symbol=...`

### Symbols / Leaderboard

- `GET /symbols/price/trending`
- `GET /leaderboard/sorted?pageNumber=...&pageSize=...`
- `GET /leaderboard/user/{userId}`
- `POST /leaderboard/update?contestId=...`

## Scheduled Jobs

Active jobs (with ShedLock):

- Contest status maintenance: every `30s`
- Investment order processing: every `5s`
- Investment value maintenance: every `5s`
- Leaderboard job processing: every `5s`
- Leaderboard job creation: every `15s`

The ShedLock table is created by Flyway (`shedlock`).

## Database and Migrations

Flyway migrations are located in `src/main/resources/db/migration`.

Core tables:

- `t_user`, `t_user_subject`
- `t_contest`
- `t_participant`
- `t_investment`, `t_investment_order`
- `t_leaderboard`, `t_leaderboard_entry`, `t_leaderboard_job`, `t_medal`
- `shedlock`

## Observability

- Actuator endpoints include health/info/prometheus/env
- Prometheus scrape path: `/actuator/prometheus`
- `local` profile: console logging only
- non-`local` profile: console + Loki appender (`loki.grafana-stack:3100`)

Kubernetes observability files are under `k8s/observability`.

## Kubernetes

Key files:

- `k8s/stockcomp.yaml`
- `k8s/postgres/postgresql.yaml`
- `k8s/postgres/postgres-persistence.yaml`
- `skaffold.yaml`

Skaffold image build is configured through Jib (`stockcomp-server-image`).

## CI

GitHub Actions workflows:

- `build.yml`: unit tests, integration tests, Docker build/push
- `ktlint.yml`: formatting checks

## Practical Notes

- Endpoints requiring market prices depend on FastFinance (`fastfinance.base.url`).
- For local development without FastFinance, you can temporarily disable scheduling with `scheduling.enabled=false`.
- Integration tests fail if no container runtime (Docker/Podman/Colima) is available.
- There is no explicit admin bootstrap in application code; ADMIN access requires at least one user with `user_role=ADMIN` in the database.
