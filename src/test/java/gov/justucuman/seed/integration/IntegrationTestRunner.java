package gov.justucuman.seed.integration;

import gov.justucuman.seed.SeedApplication;
import gov.justucuman.seed.integration.components.ArgumentAwareComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract base class for Karate-based integration tests.
 * <p>
 * This class sets up the Spring Boot application context with Testcontainers
 * and executes Karate feature files for end-to-end API testing.
 * <p>
 * <b>Important:</b> Karate tests cannot directly use Spring Boot's {@code @ServiceConnection}
 * because Karate runs in a separate JUnit lifecycle. Therefore, this class manually
 * manages the PostgreSQL container and passes connection details to Spring Boot.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * public class ProductIntegrationTest extends IntegrationTestRunner {
 *
 *     @Override
 *     public String getFeatureDirectory() {
 *         return "products";  // Loads classpath:integration/features/products/*.feature
 *     }
 * }
 * }</pre>
 *
 * @see ArgumentAwareComponent
 * @see MockServerComponent
 * @see gov.justucuman.seed.test.containers.TestContainersConfiguration
 */
@Slf4j
@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestRunner {

	/**
	 * Server port for the application during tests.
	 */
	private final Integer serverPort = 8080;

	/**
	 * Base path for Karate feature files.
	 */
	private final String basePath = "classpath:integration/features";

	/**
	 * The Spring application context.
	 */
	private ConfigurableApplicationContext context;

	/**
	 * List of components that provide custom Spring Boot arguments.
	 */
	private final List<ArgumentAwareComponent> components = new ArrayList<>();

	/**
	 * PostgreSQL container for integration tests.
	 * <p>
	 * Manually managed since Karate runs outside Spring's test context.
	 */
	private final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16-alpine")
	)
			.withDatabaseName("seed_db")
			.withUsername("dev_user")
			.withPassword("dev_password");

	/**
	 * Initializes the Spring Boot application context with test configuration.
	 * <p>
	 * This method:
	 * <ol>
	 *   <li>Starts the PostgreSQL container</li>
	 *   <li>Starts all registered {@link ArgumentAwareComponent} instances</li>
	 *   <li>Collects custom Spring Boot arguments from components</li>
	 *   <li>Configures datasource properties from the running container</li>
	 *   <li>Starts the application with test profile and custom arguments</li>
	 * </ol>
	 *
	 * @throws IOException if application context fails to start
	 * @throws InterruptedException if application startup is interrupted
	 */
	@BeforeAll
	protected void initContext() throws IOException, InterruptedException {
		log.info("Starting integration test context");

		// Start PostgreSQL container
		log.info("Starting PostgreSQL container");
		postgresContainer.start();

		// Start all registered components
		components.forEach(ArgumentAwareComponent::start);

		// Collect Spring Boot arguments from components
		List<String> arguments = components.stream()
				.flatMap(component -> component.argumentList().stream())
				.collect(Collectors.toList());

		// Add standard test configuration
		arguments.add("--server.port=" + serverPort);
		// Use Hibernate to create schema for tests (simpler than Flyway for integration tests)
		arguments.add("--spring.jpa.hibernate.ddl-auto=create-drop");
		arguments.add("--spring.flyway.enabled=false");
		arguments.add("--spring.profiles.active=test");

		// Configure datasource from the running PostgreSQL container
		arguments.add("--spring.datasource.url=" + postgresContainer.getJdbcUrl());
		arguments.add("--spring.datasource.username=" + postgresContainer.getUsername());
		arguments.add("--spring.datasource.password=" + postgresContainer.getPassword());
		arguments.add("--spring.datasource.driver-class-name=org.postgresql.Driver");

		// Disable elasticsearch for integration tests (not needed for basic CRUD tests)
		arguments.add("--elasticsearch.enabled=false");
		// Disable kafka for integration tests (not needed for basic CRUD tests)
		arguments.add("--kafka.enabled=false");

		log.info("Starting application with arguments: {}", arguments);

		// Start the Spring Boot application
		context = SpringApplication.run(SeedApplication.class, arguments.toArray(String[]::new));
	}

	/**
	 * Executes the Karate feature tests.
	 * <p>
	 * Features are loaded from {@code classpath:integration/features/{getFeatureDirectory()}.feature}
	 *
	 * @return Karate test runner configured for the feature file
	 */
	@Karate.Test
	Karate runFeatureTest() {
		return Karate.run(basePath + "/" + getFeatureDirectory() + ".feature")
				.karateEnv("test")
				.systemProperty("baseUrl", "http://localhost:" + serverPort);
	}

	/**
	 * Shuts down the Spring application context, stops all components, and stops the PostgreSQL container.
	 */
	@AfterAll
	protected void closeContext() {
		log.info("Stopping integration test context");
		if (context != null) {
			context.close();
		}
		components.forEach(ArgumentAwareComponent::stop);
		if (postgresContainer != null && postgresContainer.isRunning()) {
			postgresContainer.stop();
		}
	}

	/**
	 * Registers a component for lifecycle management.
	 * <p>
	 * Components will be started before the application context and stopped after all tests.
	 *
	 * @param component the component to register
	 */
	protected void registerComponent(ArgumentAwareComponent component) {
		components.add(component);
	}

	/**
	 * Returns the feature file name for this test class (without the .feature extension).
	 * <p>
	 * Feature files should be located in {@code src/test/resources/integration/features/}
	 *
	 * @return the feature file name without extension (e.g., {@code "products"})
	 */
	public abstract String getFeatureDirectory();

}
