plugins {
	java
	id("org.springframework.boot") version "4.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.flywaydb.flyway") version "11.11.0"
	id("checkstyle")
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-kafka")

	// Data Access (JPA, PostgreSQL, Redis)
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")

	// OpenAPI (Swagger UI) - Spring Boot 4 compatible version
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

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

	// Karate for integration testing
	testImplementation("com.intuit.karate:karate-core:1.4.1")
	testImplementation("com.intuit.karate:karate-junit5:1.4.1")
	testImplementation("net.masterthought:cucumber-reporting:5.8.4")
	testImplementation("org.apache.commons:commons-lang3:3.17.0")

	// Testcontainers - Spring Boot 4 uses version 2.0
	testImplementation("org.mock-server:mockserver-client-java:5.15.0")
	testImplementation("org.testcontainers:testcontainers:1.20.4")
	testImplementation("org.testcontainers:junit-jupiter:1.20.4")
	testImplementation("org.testcontainers:mockserver:1.20.4")
	testImplementation("org.testcontainers:postgresql:1.20.4")
	testImplementation("org.testcontainers:elasticsearch:1.20.4")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Flyway configuration
flyway {
	url = "jdbc:postgresql://localhost:5432/seed_db"
	user = "dev_user"
	password = "dev_password"
}

// JaCoCo configuration for code coverage
jacoco {
	toolVersion = "0.8.12"
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

	violationRules {
		rule {
			// Overall project: 60% minimum
			limit {
				minimum = 0.60.toBigDecimal()
			}
		}

		rule {
			// Domain layer: 80% minimum
			limit {
				minimum = 0.80.toBigDecimal()
			}
			includes = listOf("gov.justucuman.seed.domain.**")
		}

		rule {
			// Application layer: 70% minimum
			limit {
				minimum = 0.70.toBigDecimal()
			}
			includes = listOf("gov.justucuman.seed.application.**")
		}

		rule {
			// Infrastructure layer: 50% minimum
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
