package gov.justucuman.seed.integration;

import gov.justucuman.seed.SeedApplication;
import gov.justucuman.seed.integration.components.ArgumentAwareComponent;
import gov.justucuman.seed.integration.components.MockServerComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
 * <b>Spring Boot 4 Enhancements:</b>
 * <ul>
 *   <li>Can leverage {@code @ServiceConnection} for automatic container configuration</li>
 *   <li>Compatible with {@code @ImportTestcontainers} for declarative container management</li>
 *   <li>Supports Java 25 records and pattern matching</li>
 * </ul>
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
 * <p>
 * <b>Alternative Spring Boot 4 approach:</b>
 * For simpler tests, you can use Spring Boot 4's native testcontainers support:
 * <pre>{@code
 * @SpringBootTest
 * @Import(TestContainersConfiguration.class)
 * class MyIntegrationTest {
 *
 *     @Autowired
 *     private MockServerClient mockServerClient;
 *
 *     @Test
 *     void testWithKarate() {
 *         Karate.run("classpath:integration/features/my-test.feature");
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
	 * Initializes the Spring Boot application context with test configuration.
	 * <p>
	 * This method:
	 * <ol>
	 *   <li>Starts all registered {@link ArgumentAwareComponent} instances</li>
	 *   <li>Collects custom Spring Boot arguments from components</li>
	 *   <li>Starts the application with test profile and custom arguments</li>
	 * </ol>
	 *
	 * @throws IOException if application context fails to start
	 * @throws InterruptedException if application startup is interrupted
	 */
	@BeforeAll
	protected void initContext() throws IOException, InterruptedException {
		log.info("Starting integration test context");

		// Start all registered components
		components.forEach(ArgumentAwareComponent::start);

		// Collect Spring Boot arguments from components
		List<String> arguments = components.stream()
				.flatMap(component -> component.argumentList().stream())
				.collect(Collectors.toList());

		// Add standard test configuration
		arguments.add("--server.port=" + serverPort);
		arguments.add("--spring.sql.init.mode=always");
		arguments.add("--spring.profiles.active=test");

		log.info("Starting application with arguments: {}", arguments);

		// Start the Spring Boot application
		context = SpringApplication.run(SeedApplication.class, arguments.toArray(String[]::new));
	}

	/**
	 * Executes the Karate feature tests.
	 * <p>
	 * Features are loaded from {@code classpath:integration/features/{getFeatureDirectory()}/*.feature}
	 *
	 * @return Karate test runner configured for the feature directory
	 */
	@Karate.Test
	Karate runFeatureTest() {
		return Karate.run(basePath + "/" + getFeatureDirectory());
	}

	/**
	 * Shuts down the Spring application context and stops all components.
	 */
	@AfterAll
	protected void closeContext() {
		log.info("Stopping integration test context");
		if (context != null) {
			context.close();
		}
		components.forEach(ArgumentAwareComponent::stop);
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
	 * Returns the feature directory for this test class.
	 * <p>
	 * Feature files should be located in {@code src/test/resources/integration/features/{directory}/}
	 *
	 * @return the feature directory name (e.g., {@code "products"})
	 */
	public abstract String getFeatureDirectory();

}
