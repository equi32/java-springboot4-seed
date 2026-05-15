plugins {
	java
	id("org.springframework.boot") version "4.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.flywaydb.flyway") version "11.11.0"
	id("checkstyle")
	id("com.diffplug.spotless") version "7.0.4"
	jacoco
}

group = "gov.justucuman"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot 4 Seed"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

checkstyle {
	toolVersion = "10.21.2"
	configFile = file("${project.rootDir}/config/codestyle/checks.xml")
}

// Spotless: auto-formatter that runs palantir-java-format (4-space, classic Java style)
// over all Java sources. `./gradlew spotlessApply` rewrites files in place; `spotlessCheck`
// runs as part of `check` and fails the build if anything is unformatted.
// Palantir was chosen over google-java-format because the codebase is 4-space indented and
// Palantir's graph-based line wrapping leaves single-line method calls alone when they fit
// (Google explodes them aggressively, producing a much noisier diff).
spotless {
	java {
		target("src/**/*.java")
		// MapStruct generates *Impl.java under build/generated/...; Spotless's default
		// target already excludes build/, but be explicit so future generators don't sneak in.
		targetExclude("build/**", "**/generated/**")
		// indentWithSpaces runs BEFORE palantirJavaFormat so it normalizes leading tabs
		// inside Java text blocks (""") too — palantir intentionally never touches text
		// block content, so without this step checkstyle's FileTabCharacter would still
		// fire on JSON/SQL embedded in test files.
		indentWithSpaces(4)
		// 2.71.0+ is required for JDK 25 compatibility (fixes a NoSuchMethodError
		// on DeferredDiagnosticHandler.getDiagnostics whose return type changed in
		// JDK 25). 2.90.0 is the latest stable as of this configuration.
		palantirJavaFormat("2.90.0")
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-kafka")

	// OpenTelemetry: Spring Boot 4's official starter bundles the OTel SDK autoconfig,
	// Micrometer tracing -> OTel bridge, OTLP exporters for traces/metrics/logs.
	// Replaces hand-picked micrometer-tracing-bridge-otel + opentelemetry-exporter-otlp + micrometer-registry-otlp,
	// because in Boot 4 the tracing/logs autoconfig lives in dedicated modules
	// (spring-boot-micrometer-tracing-opentelemetry, spring-boot-opentelemetry) that this starter pulls in.
	implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
	implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.16.0-alpha")
	// datasource-micrometer-spring-boot 1.0.5 is incompatible with Spring Boot 4
	// (references org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer
	// which was moved/removed in Boot 4). Revisit once a Boot-4-compatible release is published.
	implementation("net.logstash.logback:logstash-logback-encoder:7.4")

	// HTTP request/response logging (referenced by org.zalando.logbook.Logbook logger in logback-spring.xml)
	// 4.x line adds Spring Boot 4 + Jackson 3 support. See https://stevenpg.com/posts/spring-boot-4-logbook-now-works/
	implementation("org.zalando:logbook-spring-boot-starter:4.0.4")

	// Data Access (JPA, PostgreSQL, Redis)
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")

	// OpenAPI (Swagger UI) - 3.x line targets Spring Boot 4 / Jackson 3.
	// 2.x is pinned to Spring Boot 3 / Jackson 2 and is not compatible.
	// 3.0.3 is currently the latest 3.x release published to Maven Central; no 3.0.4+
	// exists yet, so any vulnerability scanner hits here cannot be resolved by an upgrade.
	// Revisit when springdoc publishes a newer 3.x.
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	// Flyway
	implementation("org.flywaydb:flyway-core:11.11.0")
	implementation("org.flywaydb:flyway-database-postgresql:11.11.0")

	// Kafka - Spring Boot 4 uses Kafka 4.0
	//implementation("org.springframework.kafka:spring-kafka")

	// OpenSearch - Using opensearch-java client directly
	// We don't use spring-boot-starter-elasticsearch to avoid Jackson 2/3 conflicts in Spring Boot 4
	// We provide custom OpenSearchClient bean with authentication
	implementation("org.opensearch.client:opensearch-java:2.19.0")
	implementation("org.opensearch.client:opensearch-rest-client:2.19.0")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	// Property migrator for runtime diagnostics (remove after migration)
	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")

	// ArchUnit - hexagonal architecture rule enforcement (fails the build on layer violations)
	testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")

	// Karate for integration testing
	testImplementation("io.karatelabs:karate-core:1.5.0")
	testImplementation("io.karatelabs:karate-junit5:1.5.0")
	testImplementation("net.masterthought:cucumber-reporting:5.8.6")
	// commons-lang3 < 3.18.0 has CVE-2025-48924 (uncontrolled recursion in
	// ClassUtils.getClass). Declared as testImplementation to force-up the version
	// Karate pulls transitively; no source code imports org.apache.commons.lang3.*.
	testImplementation("org.apache.commons:commons-lang3:3.20.0")

	// Testcontainers - Spring Boot 4 uses version 2.0
	// mockserver-client-java 5.15.0 is the latest release (Jan 2023 — the project has
	// not published since). Reported Netty CVEs (CVE-2025-67735, CVE-2025-58057, ...)
	// live in mockserver-netty (the server), which is NOT on our classpath — we run
	// MockServer as a Docker container via Testcontainers. The client version is also
	// pinned to the container image tag "mockserver/mockserver:5.15.0" in
	// MockServerComponent.java, so any future bump must update both in lockstep.
	testImplementation("org.mock-server:mockserver-client-java:5.15.0")
	testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.5"))
	testImplementation("org.testcontainers:testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-mockserver")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.testcontainers:testcontainers-elasticsearch")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Flyway is configured exclusively via src/main/resources/application.yaml
// (spring.flyway.*) so the Spring Boot autoconfig runs migrations at app
// startup using env-driven values. No Gradle-side override here.

// JaCoCo configuration for code coverage
jacoco {
	toolVersion = "0.8.14"
}

// Tests are now enabled with Spring Boot 4 integration test infrastructure
// Tests use Karate + Testcontainers for end-to-end API testing

// Configure test task to generate JaCoCo reports
tasks.withType<Test> {
	useJUnitPlatform()

	jacoco {
		excludes.addAll(listOf(
			// DTOs and Records
			"**/dto/**",
			"**/response/**",
			"**/request/**",
			"**/document/**",
			"**/event/**",
			// Configuration
			"**/config/**",
			// MapStruct generated classes
			"**/mapper/**/*Impl.class",
			// Main application
			"**/SeedApplication.class",
			// Exceptions
			"**/exception/**",
			// Constants and enums
			"**/constants/**",
			"**/ProblemType.class"
		))
	}
}

// Generate HTML JaCoCo reports
tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		html.required.set(true)
		xml.required.set(true)
		csv.required.set(false)

		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
		xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
	}

	classDirectories.setFrom(
		files(classDirectories.files.map { dir ->
			fileTree(dir).apply {
				exclude("**/dto/**")
				exclude("**/response/**")
				exclude("**/request/**")
				exclude("**/document/**")
				exclude("**/event/**")
				exclude("**/config/**")
				exclude("**/mapper/**/*Impl.class")
				exclude("**/SeedApplication.class")
				exclude("**/exception/**")
				exclude("**/constants/**")
				exclude("**/ProblemType.class")
			}
		})
	)
}

// Verify coverage thresholds
tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.jacocoTestReport)

	// Apply the same exclusions as jacocoTestReport so verification doesn't fail on
	// generated/boilerplate code (DTOs, exceptions, configs, MapStruct Impls, ...).
	classDirectories.setFrom(
		files(classDirectories.files.map { dir ->
			fileTree(dir).apply {
				exclude("**/dto/**")
				exclude("**/response/**")
				exclude("**/request/**")
				exclude("**/document/**")
				exclude("**/event/**")
				exclude("**/config/**")
				exclude("**/mapper/**/*Impl.class")
				exclude("**/SeedApplication.class")
				exclude("**/exception/**")
				exclude("**/constants/**")
				exclude("**/ProblemType.class")
			}
		})
	)

	violationRules {
		rule {
			// Overall project: 60% minimum
			limit {
				minimum = 0.60.toBigDecimal()
			}
		}

		rule {
			// Domain layer: 90% minimum
			// element = PACKAGE so the includes filter actually matches; with the default
			// BUNDLE element, "gov.justucuman.seed.domain.**" never matches the bundle's name
			// and the rule silently no-ops.
			element = "PACKAGE"
			limit {
				minimum = 0.90.toBigDecimal()
			}
			includes = listOf("gov.justucuman.seed.domain.**")
		}

		rule {
			// Application layer: 70% minimum (evaluated per package — see comment above)
			element = "PACKAGE"
			limit {
				minimum = 0.70.toBigDecimal()
			}
			includes = listOf("gov.justucuman.seed.application.**")
		}

		rule {
			// Infrastructure layer: 50% minimum (evaluated per package — see comment above)
			element = "PACKAGE"
			limit {
				minimum = 0.50.toBigDecimal()
			}
			includes = listOf("gov.justucuman.seed.infrastructure.**")
		}
	}
}

// Make 'check' depend on coverage verification
tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
}

// Generate reports after tests
tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

// Guard against accidental deletion of the hexagonal-architecture test or its rules.
// Fails ./gradlew check if the file is missing or has fewer than EXPECTED_ARCH_RULES
// @ArchTest annotations. The count threshold should also be CODEOWNER-guarded so it
// can't be lowered without architect approval.
val expectedArchRules = 5
val architectureTestFile = file(
	"src/test/java/gov/justucuman/seed/unit/architecture/HexagonalArchitectureTest.java"
)

tasks.register("verifyArchitectureTestExists") {
	description = "Fails if the hexagonal architecture test is missing or has lost rules"
	group = "verification"

	doLast {
		if (!architectureTestFile.exists()) {
			throw GradleException(
				"Required architecture test file is missing: " +
					"${architectureTestFile.relativeTo(rootDir)}\n" +
					"This file enforces hexagonal architecture boundaries and must not be deleted. " +
					"If you genuinely need to change the rules, edit them in place — do not remove the file."
			)
		}
		val ruleCount = Regex("@ArchTest").findAll(architectureTestFile.readText()).count()
		if (ruleCount < expectedArchRules) {
			throw GradleException(
				"${architectureTestFile.name} has $ruleCount @ArchTest rules but at least " +
					"$expectedArchRules are required.\n" +
					"Restore the deleted rules, or — if the change is intentional and approved — " +
					"update expectedArchRules in build.gradle.kts."
			)
		}
	}
}

tasks.check {
	dependsOn(tasks.named("verifyArchitectureTestExists"))
}
