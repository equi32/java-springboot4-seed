# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

**Spring Boot 4.1.0-SNAPSHOT experimental seed project** with Java 25 and Hexagonal Architecture (Ports & Adapters).

This is the experimental Spring Boot 4 seed, located at `springboot4-seed/`. The main seed project is at `../seed-gradle-kotlin/`.

## Architecture: Hexagonal (Ports & Adapters)

```
src/main/java/gov/justucuman/seed/
├── domain/          # Business core - entities, port interfaces (NO external deps)
│   ├── model/       # Domain models (using Lombok @With for immutability helpers)
│   └── port/
│       ├── in/      # Use case interfaces (e.g., CreateProduct.java)
│       └── out/     # Repository/event interfaces (e.g., ProductSavePort.java)
├── application/     # Use case implementations (one granular use case per operation)
├── infrastructure/  # Adapters for external systems
│   └── adapter/
│       ├── input/
│       │   ├── rest/        # REST controllers + DTOs + mappers
│       │   └── event/kafka/ # Kafka consumers
│       └── output/
│           ├── persistence/ # JPA/JDBC adapters
│           ├── search/      # OpenSearch/Elasticsearch
│           ├── event/       # Kafka publishers
│           └── external/    # WebClient for external APIs
└── common/          # Shared utilities, error handling, constants
```

**Key Pattern**: Use case interfaces in `domain/port/in/` are implemented by classes in `application/`. Output ports in `domain/port/out/` are implemented by adapters in `infrastructure/adapter/output/`.

## Build and Run

```bash
./gradlew clean build    # Full clean build
./gradlew bootRun        # Run application (defaults to local profile)
./gradlew bootRun --args='--spring.profiles.active=dev'  # Run with dev profile
```

## Testing

```bash
./gradlew test                           # Run all tests
./gradlew test --tests ProductIntegrationTest  # Run single test class
./gradlew jacocoTestReport              # Generate coverage report (HTML at build/reports/jacoco/html/index.html)
./gradlew jacocoTestCoverageVerification # Verify coverage thresholds
./gradlew check                          # Run checkstyle + coverage verification
```

**Test Coverage Requirements** (enforced by JaCoCo):
- Domain layer: 80% minimum
- Application layer: 70% minimum
- Infrastructure layer: 50% minimum
- Overall: 60% minimum

Excluded from coverage: DTOs, records, configs, MapStruct generated code, exceptions, constants.

### Integration Tests

Integration tests use **Karate** + **Testcontainers**:

- Feature files: `src/test/resources/integration/features/*.feature`
- Test classes extend `IntegrationTestRunner` for Karate tests
- Spring Boot 4 native approach: use `@Import(TestContainersConfiguration.class)` with `@ServiceConnection`

Example Karate test:
```java
public class ProductIntegrationTest extends IntegrationTestRunner {
    @Override
    public String getFeatureDirectory() {
        return "products";  // Loads classpath:integration/features/products/*.feature
    }
}
```

Example Spring Boot 4 testcontainers test:
```java
@SpringBootTest
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class MyIntegrationTest {
    @Autowired
    private MockServerClient mockServerClient;

    @Test
    void testWithMockServer() {
        // Test using auto-injected MockServerClient
    }
}
```

## Database

```bash
./gradlew flywayMigrate    # Run database migrations
./gradlew flywayClean      # Clean database schema
```

Migrations are in `src/main/resources/db/migration/`.

## Technology Stack

- **Core**: Spring Boot 4.1.0-SNAPSHOT, Java 25
- **Web**: Spring Web, Spring WebFlux (reactive WebClient)
- **Persistence**: Spring Data JPA, JDBC Template, PostgreSQL, Flyway 11.11.0
- **Messaging**: Spring Kafka 4.0
- **Search**: OpenSearch Java client 2.19.0 (not spring-boot-starter-elasticsearch due to Jackson 2/3 conflicts in Spring Boot 4)
- **Mapping**: MapStruct 1.6.3
- **Validation**: Jakarta Bean Validation
- **Testing**: JUnit 5, Karate 1.4.1, Testcontainers 1.20.4, MockServer

## Configuration Profiles

Three environments via Spring profiles:
- **local** (default) - `application-local.yml`
- **dev** - `application-dev.yml`
- **test** - `application-test.yml`

## Package Naming

Base package: `gov.justucuman.seed`

## Important Notes

- Domain models use **Java Records** with Lombok `@With` for immutable update helpers
- **MapStruct** mappers are generated at build time
- **Kafka topics** are not auto-created in this project (configure as needed)
- **OpenSearch** uses Java client directly (avoiding spring-boot-starter-elasticsearch)
- **External APIs** use reactive `WebClient` from WebFlux
- Global exception handling is in `common/error/`
