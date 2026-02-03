package gov.justucuman.seed.integration;

/**
 * Integration test for health check endpoints using Karate.
 * <p>
 * This test demonstrates the basic IntegrationTestRunner pattern
 * for running Karate feature tests against the running Spring Boot application.
 * <p>
 * The test:
 * <ol>
 *   <li>Starts PostgreSQL container via Testcontainers</li>
 *   <li>Initializes the Spring Boot application context</li>
 *   <li>Executes Karate scenarios from {@code classpath:integration/features/health.feature}</li>
 * </ol>
 *
 * @see IntegrationTestRunner
 */
public class HealthCheckIntegrationTest extends IntegrationTestRunner {

	@Override
	public String getFeatureDirectory() {
		return "health";
	}

}
