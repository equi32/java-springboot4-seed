package gov.justucuman.seed.test.containers;

import org.mockserver.client.MockServerClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MockServerContainer;

/**
 * Test configuration for Testcontainers integration with Spring Boot 4.
 * <p>
 * This class leverages Spring Boot 4's new testing features:
 * <ul>
 *   <li>{@code @ImportTestcontainers} - imports container declarations from interfaces</li>
 *   <li>{@code @ServiceConnection} - automatic service connection configuration</li>
 *   <li>{@code @TestConfiguration} - test-specific configuration class</li>
 * </ul>
 * <p>
 * The containers declared in {@link TestContainers} are automatically started
 * and their connection properties are configured without manual {@code @DynamicPropertySource}.
 * <p>
 * Usage in tests:
 * <pre>{@code
 * @SpringBootTest
 * @Import(TestContainersConfiguration.class)
 * class MyIntegrationTest {
 *     // Tests here
 * }
 * }</pre>
 *
 * @see TestContainers
 * @see org.springframework.boot.testcontainers.context.ImportTestcontainers
 * @see org.springframework.boot.testcontainers.service.connection.ServiceConnection
 */
@TestConfiguration(proxyBeanMethods = false)
@ImportTestcontainers(TestContainers.class)
public class TestContainersConfiguration {

	/**
	 * Creates a {@link MockServerClient} bean connected to the running MockServer container.
	 * <p>
	 * This bean can be injected into tests to set up expectations:
	 * <pre>{@code
	 * @Autowired
	 * private MockServerClient mockServerClient;
	 *
	 * @Test
	 * void testWithMockServer() {
	 *     mockServerClient.when(request()
	 *         .withPath("/api/external"))
	 *         .respond(response()
	 *         .withStatusCode(200)
	 *         .withBody(json("{\"result\": \"success\"}")));
	 * }
	 * }</pre>
	 *
	 * @return configured MockServerClient instance
	 */
	@Bean
	@ServiceConnection
	public MockServerClient mockServerClient(MockServerContainer mockServerContainer) {
		return new MockServerClient(
				mockServerContainer.getHost(),
				mockServerContainer.getServerPort()
		);
	}

}
