# Spring Boot 4 Seed

A production-ready microservice template built with **Spring Boot 4**, **Java 25**, and **Hexagonal Architecture** (Ports & Adapters). Designed as a starting point for backend microservices, with opinionated defaults for persistence, messaging, search, observability, and testing.

## Why this seed?

- **Hexagonal by construction** — domain code has zero framework imports. Every adapter is pluggable: swap Postgres for another store, OpenSearch for Elasticsearch, Kafka for RabbitMQ, without touching use cases.
- **Spring Boot 4 + Java 25** — uses the latest releases and the new `spring-boot-starter-opentelemetry`, Jackson 3, Spring Web with virtual-thread-friendly defaults, and Hibernate 7.
- **Observability is built in, not bolted on** — traces, metrics, and logs are exported via OTLP from day one, fully decoupled from any specific backend (Grafana stack provided as a sample).
- **Testing without mocks where it matters** — integration tests use real Postgres + OpenSearch via Testcontainers 2.0 and Karate. No `MockMvc`-only happy paths.
- **Strict quality gates** — Checkstyle (Google Style), JaCoCo with per-layer thresholds, and a single `./gradlew check` that gates merges.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1.0-SNAPSHOT, Java 25 |
| Build | Gradle 9.3 (Kotlin DSL) |
| Database | PostgreSQL 16, JPA/Hibernate 7, Flyway 11 |
| Messaging | Apache Kafka (Spring Kafka 4.0) |
| Search | OpenSearch Java client 2.19 |
| HTTP Client | Spring WebFlux WebClient |
| API Docs | SpringDoc OpenAPI 3.0 (Swagger UI) |
| Mapping | MapStruct 1.6.3, Lombok |
| Observability | OpenTelemetry (traces + metrics + logs via OTLP), Micrometer, Logbook |
| Testing | JUnit 5, Karate 1.5, Testcontainers 2.0 |
| Quality | Checkstyle (Google Style), JaCoCo |

## Architecture

This project follows **Hexagonal Architecture** — business logic is isolated from frameworks and infrastructure. Dependencies flow inward only: `infrastructure → application → domain`. The `domain/` layer has zero framework imports.

```
src/main/java/gov/justucuman/seed/
├── domain/                       Pure Java: models, ports (interfaces)
│   ├── model/                    Records with Lombok @With for immutable updates
│   └── port/
│       ├── in/                   Use case interfaces (CreateProduct, SearchProduct, ...)
│       └── out/                  Repository / event / search interfaces
├── application/                  Use case implementations (one class per operation)
└── infrastructure/
    └── adapter/
        ├── input/
        │   ├── rest/             Spring MVC controllers, DTOs, mappers
        │   └── event/kafka/      Kafka consumers
        └── output/
            ├── persistence/      JPA (writes) + JDBC (reads)
            ├── search/           OpenSearch adapter + no-op fallback
            ├── event/            Kafka producers + no-op fallback
            └── external/         WebClient for external APIs
```

### Why granular use cases?

Each port-in interface represents **one** business operation (`CreateProduct`, `IndexProduct`, `SearchProduct`, `DeleteProductById`, ...). Each implementation in `application/` does exactly that one thing. Result: easy to test, easy to reason about, no god-services.

### Why conditional adapters?

The search and event ports each have **two implementations** selected by property:

| Property | Active adapter |
|---|---|
| `elasticsearch.enabled=true` (default) | `ProductElasticsearchAdapter` |
| `elasticsearch.enabled=false` | `NoOpProductSearchAdapter` |
| `kafka.enabled=true` (default) | `ProductKafkaPublisherAdapter` |
| `kafka.enabled=false` | `NoOpProductEventPublisherAdapter` |

This lets you boot the app in environments without the full infra (e.g., a quick local smoke run with just Postgres) and keeps tests fast when they don't need every dependency.

## Getting Started

### Prerequisites

- **Java 25**
- **Docker** (for local services + integration tests via Testcontainers)
- **WSL2 users on Docker Desktop**: integration tests work out of the box with Docker Desktop's bind-mounted socket. Testcontainers 2.0 handles the redirector behavior automatically.

### Run locally — minimal (Postgres only)

The fastest path: run with Kafka and OpenSearch disabled. The app boots with the no-op adapters in place — useful for poking at REST endpoints and JPA without the full stack.

```bash
docker run -d --name seed-postgres \
  -e POSTGRES_DB=seed_db \
  -e POSTGRES_USER=dev_user \
  -e POSTGRES_PASSWORD=dev_password \
  -p 5432:5432 \
  postgres:16-alpine

./gradlew bootRun --args='--kafka.enabled=false --elasticsearch.enabled=false'
```

### Run locally — full stack

To exercise the event-driven indexing flow (Create → Kafka → consumer → OpenSearch → searchable):

```bash
# Postgres
docker run -d --name seed-postgres -e POSTGRES_DB=seed_db -e POSTGRES_USER=dev_user \
  -e POSTGRES_PASSWORD=dev_password -p 5432:5432 postgres:16-alpine

# Kafka (single-broker KRaft mode)
docker run -d --name seed-kafka -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  apache/kafka:3.8.0

# OpenSearch (security plugin disabled for local dev)
docker run -d --name seed-opensearch -p 9200:9200 \
  -e discovery.type=single-node \
  -e plugins.security.disabled=true \
  opensearchproject/opensearch:2.11.1

./gradlew bootRun
```

The API will be available at <http://localhost:8080>, Swagger UI at <http://localhost:8080/swagger-ui.html>.

### Configuration

All deployment-specific config is driven by environment variables (or vault-injected secrets). `src/main/resources/application.yaml` is a single document — no profile blocks. The `local` defaults below let you run end-to-end without any env vars set; override per environment in your deploy spec.

| Variable | Default | Description |
|---|---|---|
| `APP_ENV` | `local` | Tag applied to all emitted metrics (`env=...`) |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `dev_db` | Database name |
| `DB_USER` | `dev_user` | Database user |
| `DB_PASSWORD` | `dev_password` | Database password |
| `DB_POOL_MAX_SIZE` | `10` | Hikari max pool size |
| `DB_POOL_MIN_IDLE` | `2` | Hikari min idle connections |
| `DB_POOL_CONNECTION_TIMEOUT_MS` | `30000` | Hikari connection timeout |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker(s) |
| `KAFKA_CONSUMER_GROUP_ID` | `api-seed-consumer-group` | Consumer group id |
| `KAFKA_PRODUCER_GROUP_ID` | `api-seed-producer-group` | Producer group id |
| `KAFKA_PRODUCT_EVENTS_TOPIC` | `product-events` | Topic for `PRODUCT_CREATED` events |
| `ELASTICSEARCH_HOST` | `localhost` | OpenSearch host |
| `ELASTICSEARCH_PORT` | `9200` | OpenSearch port |
| `ELASTICSEARCH_SCHEME` | `http` | `http` or `https` |
| `ELASTICSEARCH_USERNAME` | `admin` | OpenSearch user (unused when security disabled) |
| `ELASTICSEARCH_PASSWORD` | `admin` | OpenSearch password |
| `ELASTICSEARCH_PRODUCTS_INDEX` | `products` | Index name used by the search adapter |
| `EXTERNAL_API_BASE_URL` | `https://fakestoreapi.com` | External product API |
| `EXTERNAL_API_TIMEOUT_MS` | `5000` | WebClient timeout |
| `EXTERNAL_API_RETRY_ATTEMPTS` | `3` | WebClient retry attempts |
| `API_PREFIX` | `MS-SEED` | OpenAPI prefix label |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318` | OTLP/HTTP endpoint for traces/metrics/logs |
| `OTEL_TRACES_SAMPLER_RATIO` | `1.0` | Trace sampling ratio (0.0–1.0) |

## Build & Test

```bash
# Build (compiles + runs tests + checkstyle + coverage check)
./gradlew build

# Run all tests (spins up Testcontainers automatically)
./gradlew test

# Run a single test class
./gradlew test --tests "gov.justucuman.seed.integration.ProductIntegrationTest"

# Checkstyle
./gradlew checkstyleMain

# Coverage report → build/reports/jacoco/html/index.html
./gradlew jacocoTestReport

# Aggregate gate: tests + coverage thresholds + checkstyle
./gradlew check
```

## Testing Architecture

Integration tests use **Testcontainers 2.0** + **Karate**. Docker is required but no manual setup is needed — Postgres and OpenSearch containers start automatically when tests run.

### What Spring Boot 4 brought

- **Testcontainers 2.0** is the version aligned with Spring Boot 4. Artifacts use the new `testcontainers-` prefix (e.g. `testcontainers-postgresql` instead of `postgresql`). The build imports the `testcontainers-bom:2.0.5` and pulls module versions from it.
- **`@ImportTestcontainers` + `@ServiceConnection`** — `src/test/java/.../test/containers/TestContainersConfiguration.java` declares containers in an interface, and Spring Boot wires their connection details (JDBC URL, etc.) into the application context without `@DynamicPropertySource`. See `SeedApplicationTests` for a minimal example.

### Two layers of integration tests

| Style | When to use | Example |
|---|---|---|
| **`@SpringBootTest` + `@Import(TestContainersConfiguration.class)`** | Spring-managed test that needs the real container infrastructure (e.g. context-loads, JPA repository slices). | `SeedApplicationTests` |
| **`IntegrationTestRunner` (abstract) + Karate features** | End-to-end behavior tests written as `.feature` files that hit the running app via HTTP. | `ProductIntegrationTest`, `HealthCheckIntegrationTest` |

Karate features live in `src/test/resources/integration/features/`. A subclass of `IntegrationTestRunner` only needs to override `getFeatureDirectory()` to wire up a new feature file.

### The Karate ↔ Spring bridge

Karate runs in its own JUnit lifecycle, *outside* Spring's test context — so it can't `@Autowired` anything. For scenarios that need to call Spring beans directly (e.g. forcing a synchronous OpenSearch index when Kafka is disabled in tests), `KarateBridge` exposes a static surface that feature files invoke via Karate's `Java.type(...)`:

```gherkin
* eval Java.type('gov.justucuman.seed.integration.karate.KarateBridge').indexAndRefresh(created)
```

`IntegrationTestRunner.initContext()` calls `KarateBridge.initialize(context, indexName)` after Spring starts, handing the bridge a live `ApplicationContext`. The bridge resolves beans on demand from feature steps. Use it sparingly — most scenarios should be pure HTTP.

### Test profile defaults

`src/test/resources/application-test.yml` is auto-loaded when `@ActiveProfiles("test")` is set. `IntegrationTestRunner` additionally passes container connection details and `--kafka.enabled=false` as command-line args (the Karate flow indexes synchronously via the bridge, so the broker isn't needed).

### WSL2 + Docker Desktop

If you're on WSL2 with Docker Desktop, Testcontainers 2.0 detects the bind-mounted socket automatically. Earlier 1.x versions failed against Docker Desktop's redirector socket — if you ever pin Testcontainers back to 1.20.x, set:

```properties
# ~/.testcontainers.properties
docker.host=unix\:///mnt/wsl/docker-desktop-bind-mounts/<distro>/docker.sock
```

where `<distro>` is your WSL distro name (e.g. `Ubuntu`).

## Coverage Thresholds

Enforced by JaCoCo on every `./gradlew check`:

| Layer | Minimum |
|---|---|
| Overall | 60% |
| `domain/` | 90% |
| `application/` | 70% |
| `infrastructure/` | 50% |

Excluded from coverage: DTOs, records, configs, MapStruct generated code, exceptions, constants, Kafka adapters under `event/**`.

### Why coverage differs by layer

A flat target across the whole codebase (e.g. uniform 80%) is a common anti-pattern in Hexagonal Architecture projects. It pushes teams toward writing low-value tests against framework glue code (mocking `EntityManager`, `KafkaTemplate`, `WebClient`) while undertesting the business logic where bugs actually hurt. This project uses **differentiated, layer-aware thresholds** instead:

- **`domain/` — 90%.** The domain has zero framework dependencies and contains the business rules. It is cheap and fast to unit test, and bugs here have the largest blast radius. The bar is intentionally the strictest in the project.
- **`application/` — 70%.** Use cases are mostly orchestration over domain + ports. They are exercised both by unit tests (with port doubles) and by integration tests (which JaCoCo may credit only partially). A medium bar keeps coverage meaningful without forcing redundant tests.
- **`infrastructure/` — 50%.** Adapters (REST, JPA, Kafka, OpenSearch) are thin glue to external systems. Their real correctness is validated by the **Karate + Testcontainers** integration tests (`./gradlew test` boots real Postgres/OpenSearch), not by unit tests with mocks. Forcing 80% here typically produces mock-the-framework tests that catch no real bugs.
- **Overall — 60%.** A realistic weighted aggregate given the layer mix and the exclusion list (DTOs, configs, generated mappers, exceptions, constants).

> **Note.** Coverage is a proxy for test quality, not the goal itself. If you want a stronger signal on the domain and application layers, add **mutation testing** (e.g. [PIT](https://pitest.org/)) — it measures whether tests actually detect injected faults, which line coverage can't tell you. The differentiated thresholds above are what mature teams using Hexagonal/Clean Architecture tend to converge on in practice.

## Architecture Rules (ArchUnit)

The hexagonal layering is enforced at build time by **ArchUnit** (`HexagonalArchitectureTest`). Violations fail `./gradlew check`, so a bad import is caught the same way a failing test or coverage gap is.

| Rule | What it enforces |
|---|---|
| `domain_should_be_framework_agnostic` | Classes under `domain/**` must not import Spring, JPA/Hibernate, Kafka, OpenSearch, MapStruct, Jackson, Reactor, springdoc, etc. The hexagon's core stays pure Java. |
| `domain_should_not_depend_on_application_or_infrastructure` | Dependencies flow inward only: `infrastructure → application → domain`. Domain never reaches outward. |
| `application_should_not_depend_on_infrastructure` | Use cases must not know about adapters — they depend on ports declared in the domain. |
| `port_in_should_be_implemented_only_in_application` | Implementations of `domain/port/in/*` interfaces (use cases) must live under `application/**`. |
| `port_out_should_be_implemented_only_in_infrastructure_output` | Implementations of `domain/port/out/*` interfaces (adapters) must live under `infrastructure/adapter/output/**`. |

When a rule fails, the test report at `build/reports/tests/test/.../HexagonalArchitectureTest/<rule-name>.html` lists the exact offending classes and dependencies.

## Code Style

Enforced via **Checkstyle 10.21.2** with Google Java Style:
- 2-space indentation
- 100-character line limit
- Configuration: `config/codestyle/checks.xml`

## API

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/products` | Create a product |
| `GET` | `/api/v1/products` | List all products |
| `GET` | `/api/v1/products/{id}` | Get product by ID |
| `PUT` | `/api/v1/products/{id}` | Update a product |
| `DELETE` | `/api/v1/products/{id}` | Delete a product |
| `GET` | `/api/v1/products/search?term=<field>&value=<text>` | Search products (OpenSearch) |
| `GET` | `/api/v1/external/products` | List external products (proxy) |
| `GET` | `/api/v1/external/products/{id}` | Get external product by ID |

### Management endpoints

| Path | Description |
|---|---|
| `/health/liveness` | Liveness check |
| `/actuator/metrics` | Metrics |
| `/actuator/prometheus` | Prometheus metrics |
| `/swagger-ui.html` | Interactive API documentation |
| `/v3/api-docs` | OpenAPI spec (JSON) |

## Database Schema

Schema is managed exclusively by **Flyway** (`src/main/resources/db/migration/`). Hibernate DDL is disabled in production (`ddl-auto: none`). Integration tests use `ddl-auto=create-drop` to skip Flyway for fast container startup.

Current schema version: `1.1.0` — `products` table with UUID primary key, name, description, price, stock, status, and timestamps.

## Event Flow

When a product is created via REST:

1. `POST /api/v1/products` → `CreateProductController` → `CreateProductUseCase`
2. Product persisted to PostgreSQL via `ProductSaveJpaAdapter`
3. `PRODUCT_CREATED` event published to Kafka topic `product-events` via `ProductKafkaPublisherAdapter`
4. `ProductKafkaConsumer` receives the event → `IndexProductUseCase` → `ProductElasticsearchAdapter` indexes the product in OpenSearch
5. Product becomes searchable via `GET /api/v1/products/search`

In test profile, step 3 is skipped (Kafka disabled) and step 4 is invoked directly via `KarateBridge.indexAndRefresh(...)` from feature steps that need to verify search.

## Observability (OpenTelemetry)

The service emits **traces, metrics, and logs** over **OTLP/HTTP** to a backend-agnostic OpenTelemetry Collector. The collector then fans the three signals out to specialized stores. Endpoint is driven by `OTEL_EXPORTER_OTLP_ENDPOINT` (default `http://localhost:4318`) so the same build runs against any compatible backend.

### How it works

Spring Boot 4's `spring-boot-starter-opentelemetry` wires up the OpenTelemetry SDK, the Micrometer → OTel tracing bridge, and OTLP exporters for all three signal types. Instrumentation lives **only at framework edges** — no manual `@Observed` on use cases.

| Signal | Source | What you get |
|---|---|---|
| **Traces** | Spring Web (server), WebClient (client), Spring Kafka (producer/consumer) — auto-observed | One root span per HTTP request, child spans for outbound WebClient + Kafka publish/consume, W3C `traceparent` propagated end-to-end |
| **Metrics** | Micrometer → OTLP push (every 30s) | `http_server_request_duration_seconds_*`, `jvm_*`, `process_*`, `kafka_*`, `hikaricp_*` |
| **Logs** | Logback → `OpenTelemetryAppender` (installed in `ObservabilityConfig`) | Each log record carries the active `trace_id` / `span_id`, so logs cross-link with traces in Grafana |
| **HTTP body logging** | Zalando Logbook (4.x, Spring Boot 4 + Jackson 3 compatible) | Full request/response payloads as JSON, controlled by `logbook.*` properties |

Trace sampling is controlled by `OTEL_TRACES_SAMPLER_RATIO` (default `1.0` — sample everything; drop to `0.1` in production once volume grows).

### Local stack (Grafana + Tempo + Loki + Prometheus)

A docker-compose stack is provided to run the full pipeline locally:

```bash
docker compose -f docker-compose.observability.yml up -d
```

Pipeline:

```
seed app ──OTLP/HTTP──► OTel Collector ──┬─► Tempo       (traces)
                       (port 4317/4318)  ├─► Prometheus  (metrics, via OTLP receiver)
                                         └─► Loki        (logs)

                                         Grafana (port 3000) reads all three.
```

| Service | URL | Purpose |
|---|---|---|
| Grafana | <http://localhost:3000> | Single UI for traces/metrics/logs (anonymous admin enabled, no login) |
| OTel Collector | <http://localhost:4318> | OTLP/HTTP ingest from the app |
| OTel Collector | <http://localhost:4317> | OTLP/gRPC ingest from the app |
| Tempo | <http://localhost:3200> | Traces backend |
| Loki | <http://localhost:3100> | Logs backend |
| Prometheus | <http://localhost:9090> | Metrics backend (OTLP write receiver enabled) |
| Collector self-metrics | <http://localhost:8888/metrics> | Collector's own health |

Grafana datasources for Tempo, Loki, and Prometheus are auto-provisioned with cross-linking (click a `trace_id` in a log line → jump to the trace; click a span → jump to its logs).

### Test profile — no collector required

The `test` profile in `src/test/resources/application-test.yml` disables tracing and metric export:

```yaml
management:
  tracing:
    enabled: false
  otlp:
    metrics:
      export:
        enabled: false
```

so `./gradlew test` never needs an OTel collector running.

### Verifying the stack locally

After the stack is up and the app has handled at least one request:

```bash
# 1. Generate some traffic
curl http://localhost:8080/api/v1/products
curl http://localhost:8080/api/v1/external/products

# 2. Wait ~30s (metrics push cycle), then confirm the collector is receiving signals
docker logs seed-otelcol --since=60s | grep -iE "ResourceSpans|ResourceMetrics|ResourceLogs"

# 3. Confirm Prometheus ingested the metrics
curl -s http://localhost:9090/api/v1/label/__name__/values | jq '.data[]' | grep http_server

# 4. Open Grafana → Explore
#    - Tempo:      Search → service.name = seed
#    - Loki:       {service_name="seed"}
#    - Prometheus: rate(http_server_request_duration_seconds_count[1m])
```

### Configuration knobs

| Variable | Default | Description |
|---|---|---|
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318` | Base OTLP/HTTP endpoint; `/v1/traces`, `/v1/metrics`, `/v1/logs` are appended per signal |
| `OTEL_TRACES_SAMPLER_RATIO` | `1.0` | Trace sampling ratio (0.0–1.0) |
| `logbook.include` | `/**` | Path patterns to log HTTP bodies for |
| `logbook.format.style` | `json` | `json` or `http` (curl-style) |
| `logging.level.org.zalando.logbook` | `TRACE` | Must be `TRACE` for Logbook to emit; lower it to silence body logs |

## Project Layout

```
.
├── build.gradle.kts                Gradle build (Kotlin DSL)
├── config/codestyle/checks.xml     Checkstyle rules
├── docker/                         Observability stack configs (Grafana, Tempo, Loki, OTel, Prometheus)
├── docker-compose.observability.yml
├── src/
│   ├── main/
│   │   ├── java/gov/justucuman/seed/   See "Architecture" above
│   │   └── resources/
│   │       ├── application.yaml        Multi-profile config (default, local, dev)
│   │       ├── db/migration/           Flyway migrations
│   │       └── logback-spring.xml      Logback + OpenTelemetry appender
│   └── test/
│       ├── java/gov/justucuman/seed/
│       │   ├── integration/            Karate test runners + IntegrationTestRunner base
│       │   └── test/containers/        TestContainersConfiguration + TestContainers interface
│       └── resources/
│           ├── application-test.yml    Test-profile overrides
│           └── integration/features/   Karate .feature files
└── CLAUDE.md                       Working agreement / repo conventions
```
