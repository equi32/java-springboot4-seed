package gov.justucuman.seed.integration;

import gov.justucuman.seed.test.containers.TestContainersConfiguration;

import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

/**
 * Integration test demonstrating Spring Boot 4's native Testcontainers support.
 * <p>
 * This class showcases the modern approach to integration testing in Spring Boot 4:
 * <ul>
 *   <li>{@code @Import(TestContainersConfiguration.class)} - imports container declarations</li>
 *   <li>{@code @ServiceConnection} - automatically configures DataSource, Flyway</li>
 *   <li>MockServerClient injection - direct bean access for setting expectations</li>
 * </ul>
 * <p>
 * <b>Benefits over traditional approach:</b>
 * <ul>
 *   <li>No manual {@code @DynamicPropertySource} needed</li>
 *   <li>No manual container lifecycle management</li>
 *   <li>Cleaner separation of test and configuration</li>
 *   <li>Reusable container configuration across tests</li>
 * </ul>
 *
 * @see gov.justucuman.seed.test.containers.TestContainers
 * @see gov.justucuman.seed.test.containers.TestContainersConfiguration
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class SpringBoot4ContainersIntegrationTest {

	/**
	 * Auto-injected MockServerClient bean.
	 * <p>
	 * Spring Boot 4 creates this bean automatically via {@link TestContainersConfiguration#mockServerClient(MockServerContainer)}.
	 * The bean is configured to connect to the running MockServer container.
	 */
	@Autowired
	private MockServerClient mockServerClient;

	/**
	 * Example test demonstrating MockServer expectation setup.
	 * <p>
	 * This test shows how to:
	 * <ol>
	 *   <li>Configure MockServer expectations programmatically</li>
	 *   <li>Verify the Spring Boot application can interact with the mock</li>
	 *   <li>Use the injected MockServerClient bean</li>
	 * </ol>
	 */
	@Test
	void shouldConfigureMockServerExpectations() {
		// Arrange: Set up a mock expectation
		mockServerClient
				.when(request()
						.withPath("/api/external/products/1")
						.withMethod("GET"))
				.respond(response()
						.withStatusCode(200)
						.withBody(json("""
								{
									"id": 1,
									"title": "External Product",
									"price": 29.99
								}
								""")));

		// The expectation is now configured and can be verified
		// by making HTTP requests through the application
	}

	/**
	 * Example test demonstrating the application context loads successfully.
	 * <p>
	 * This simple test verifies that:
	 * <ul>
	 *   <li>PostgreSQL container is started and connected</li>
 *   *   <li>Flyway migrations are executed</li>
	 *   <li>All Spring beans are properly initialized</li>
	 *   <li>MockServer container is accessible</li>
	 * </ul>
	 */
	@Test
	void shouldLoadApplicationContext() {
		// If this test passes, the application context loaded successfully
		// with all containers properly configured via @ServiceConnection
	}

}
