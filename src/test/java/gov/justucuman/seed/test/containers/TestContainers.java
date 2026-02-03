package gov.justucuman.seed.test.containers;

import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

/**
 * Interface declaring Testcontainers for integration testing.
 * <p>
 * Spring Boot 4 automatically configures service connections via {@link ServiceConnection},
 * eliminating the need for manual property configuration with {@code @DynamicPropertySource}.
 * <p>
 * The {@code @ImportTestcontainers} annotation in {@link TestContainersConfiguration}
 * imports these declarations into the test application context.
 *
 * @see TestContainersConfiguration
 * @see org.springframework.boot.testcontainers.context.ImportTestcontainers
 */
public interface TestContainers {

	/**
	 * PostgreSQL container for database integration tests.
	 * <p>
	 * The {@code @ServiceConnection} annotation automatically configures:
	 * <ul>
	 *   <li>Spring DataSource properties</li>
	 *   <li>Flyway migration configuration</li>
	 *   <li>JPA/Hibernate EntityManagerFactory</li>
	 * </ul>
	 */
	@Container
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16-alpine")
	)
			.withDatabaseName("seed_db")
			.withUsername("dev_user")
			.withPassword("dev_password");

	/**
	 * MockServer container for mocking external APIs in integration tests.
	 * <p>
	 * Note: MockServer doesn't have built-in {@code @ServiceConnection} support,
	 * so the URL needs to be configured via {@code @DynamicPropertySource}
	 * or directly in the test configuration.
	 */
	@Container
	MockServerContainer mockServerContainer = new MockServerContainer(
			DockerImageName.parse("mockserver/mockserver:5.15.0")
	);

}
