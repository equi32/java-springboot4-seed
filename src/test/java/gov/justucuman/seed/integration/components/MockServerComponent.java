package gov.justucuman.seed.integration.components;

import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test component for managing MockServer with Testcontainers.
 * <p>
 * This component provides a fluent API for setting up mock HTTP expectations
 * in integration tests. It implements {@link ArgumentAwareComponent} to provide
 * the MockServer URL to the Spring Boot application context.
 * <p>
 * Spring Boot 4 enhancements:
 * <ul>
 *   <li>Works seamlessly with {@code @ServiceConnection} for automatic configuration</li>
 *   <li>Can be used alongside {@code @ImportTestcontainers} for cleaner setup</li>
 *   <li>Supports both container-based and client-based mocking strategies</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>{@code
 * // In IntegrationTestRunner:
 * MockServerComponent mockServer = MockServerComponent.getInstance();
 * mockServer.start();
 *
 * mockServer.withExpectations(List.of(
 *     new MockServerExpectation<>(
 *         "/api/external/products",
 *         Map.of("id", 1, "name", "Test Product"),
 *         200,
 *         "GET"
 *     )
 * ));
 *
 * // After test:
 * mockServer.stop();
 * }</pre>
 * <p>
 * Alternatively, with Spring Boot 4's {@code @ImportTestcontainers}:
 * <pre>{@code
 * @SpringBootTest
 * @Import(TestContainersConfiguration.class)
 * class MyTest {
 *     @Autowired
 *     private MockServerClient mockServerClient;
 *
 *     @Test
 *     void test() {
 *         mockServerClient.when(request().withPath("/api/external"))
 *             .respond(response().withStatusCode(200));
 *     }
 * }
 * }</pre>
 *
 * @see ArgumentAwareComponent
 * @see gov.justucuman.seed.test.containers.TestContainersConfiguration
 */
@Slf4j
public class MockServerComponent implements ArgumentAwareComponent {

	private final MockServerContainer mockServerContainer;

	private MockServerComponent() {
		this.mockServerContainer = new MockServerContainer(
				DockerImageName.parse("mockserver/mockserver:5.15.0")
		);
	}

	/**
	 * Creates a new instance of MockServerComponent.
	 * <p>
	 * Uses static factory method for cleaner instantiation in test setup.
	 *
	 * @return new MockServerComponent instance
	 */
	public static MockServerComponent getInstance() {
		return new MockServerComponent();
	}

	/**
	 * Returns the MockServer endpoint URL.
	 * <p>
	 * This URL can be used to configure external API clients in tests.
	 *
	 * @return MockServer endpoint URL (e.g., {@code http://localhost:xxxx})
	 */
	public String getEndpoint() {
		return mockServerContainer.getEndpoint();
	}

	@Override
	public List<String> argumentList() {
		return List.of("--external.api.product.base-url=" + getEndpoint());
	}

	@Override
	public void start() {
		mockServerContainer.start();
		log.info("MockServer started at: {}", getEndpoint());
	}

	@Override
	public void stop() {
		mockServerContainer.close();
		log.info("MockServer stopped");
	}

	/**
	 * Configures HTTP expectations on the MockServer.
	 * <p>
	 * This method sets up mock responses for HTTP requests matching the
	 * specified path and method. All responses are sent as JSON.
	 *
	 * @param <T> the response body type (will be serialized to JSON)
	 * @param expectations list of expectations to configure
	 */
	public <T> void withExpectations(List<MockServerExpectation<T>> expectations) {
		try (MockServerClient client = new MockServerClient(
				mockServerContainer.getHost(),
				mockServerContainer.getServerPort()
		)) {
			expectations.forEach(expectation -> {
				log.info("Setting up MockServer expectation: {} {} -> {}",
						expectation.getMethod(), expectation.getPath(), expectation.getResponseStatus());

				client.when(HttpRequest.request()
								.withPath(expectation.getPath())
								.withMethod(expectation.getMethod()))
						.respond(HttpResponse.response()
								.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.withStatusCode(expectation.getResponseStatus())
								.withBody(JsonBody.json(expectation.getResponse())));
			});
		}
	}

	/**
	 * Configures HTTP expectations using a fluent consumer API.
	 * <p>
	 * This method provides direct access to the MockServerClient for advanced
	 * expectation configuration.
	 *
	 * @param expectationsConsumer consumer that receives the MockServerClient
	 */
	public void withExpectations(Consumer<MockServerClient> expectationsConsumer) {
		try (MockServerClient client = new MockServerClient(
				mockServerContainer.getHost(),
				mockServerContainer.getServerPort()
		)) {
			expectationsConsumer.accept(client);
		}
	}

	/**
	 * Returns the underlying MockServerContainer for advanced use cases.
	 *
	 * @return the MockServerContainer instance
	 */
	public MockServerContainer getContainer() {
		return mockServerContainer;
	}

	/**
	 * Represents a MockServer HTTP expectation.
	 * <p>
	 * Encapsulates the request path, response body, status code, and HTTP method
	 * for a single mock expectation.
	 *
	 * @param <T> the response body type
	 */
	@Data
	@AllArgsConstructor
	public static class MockServerExpectation<T> {

		/**
		 * The request path to match (e.g., {@code /api/products}).
		 */
		private String path;

		/**
		 * The response body to return.
		 * <p>
		 * Will be serialized to JSON using the MockServer's JsonBody.
		 */
		private T response;

		/**
		 * The HTTP status code to return (e.g., {@code 200}, {@code 404}).
		 */
		private Integer responseStatus;

		/**
		 * The HTTP method to match (e.g., {@code GET}, {@code POST}).
		 */
		private String method;

	}

}
