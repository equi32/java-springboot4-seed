package gov.justucuman.seed.integration.components;

import java.util.List;

/**
 * Interface for components that can provide custom Spring Boot arguments
 * and manage their own lifecycle during integration tests.
 * <p>
 * This abstraction allows test components (like MockServer) to:
 * <ul>
 *   <li>Provide custom Spring Boot properties via {@link #argumentList()}</li>
 *   <li>Control their startup/shutdown lifecycle</li>
 *   <li>Integrate seamlessly with the test context</li>
 * </ul>
 * <p>
 * Example implementation for MockServer:
 * <pre>{@code
 * public class MockServerComponent implements ArgumentAwareComponent {
 *     private final MockServerContainer container = new MockServerContainer(...);
 *
 *     @Override
 *     public List<String> argumentList() {
 *         return List.of("--external.api.base-url=" + container.getEndpoint());
 *     }
 *
 *     @Override
 *     public void start() {
 *         container.start();
 *     }
 *
 *     @Override
 *     public void stop() {
 *         container.close();
 *     }
 * }
 * }</pre>
 *
 * @see MockServerComponent
 */
public interface ArgumentAwareComponent {

	/**
	 * Returns a list of Spring Boot arguments to be passed to the application context.
	 * <p>
	 * These arguments are typically used to configure external service URLs,
	 * mock endpoints, or other test-specific properties.
	 *
	 * @return list of Spring Boot arguments (e.g., {@code --property=value})
	 */
	List<String> argumentList();

	/**
	 * Starts the component before the application context is initialized.
	 * <p>
	 * This method is called in the {@code @BeforeAll} phase of the test lifecycle.
	 * Components should ensure all required resources are available before returning.
	 */
	void start();

	/**
	 * Stops the component after all tests have completed.
	 * <p>
	 * This method is called in the {@code @AfterAll} phase of the test lifecycle.
	 * Components should release all resources (close connections, stop containers, etc.).
	 */
	void stop();

}
