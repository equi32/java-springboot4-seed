package gov.justucuman.seed.integration;

/**
 * Integration test for Product API using Karate.
 * <p>
 * This test demonstrates full CRUD operations testing with:
 * <ul>
 *   <li>PostgreSQL database integration via Testcontainers</li>
 *   <li>Flyway migrations execution</li>
 *   <li>RESTful API endpoint testing</li>
 *   <li>Request/response validation using Karate expressions</li>
 * </ul>
 *
 * @see IntegrationTestRunner
 */
public class ProductIntegrationTest extends IntegrationTestRunner {

	@Override
	public String getFeatureDirectory() {
		return "products";
	}

}
