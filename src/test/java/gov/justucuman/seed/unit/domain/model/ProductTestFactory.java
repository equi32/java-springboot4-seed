package gov.justucuman.seed.unit.domain.model;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory class for creating test {@link Product} instances.
 * <p>
 * This centralizes product object construction for unit tests, reducing duplication
 * and providing consistent test data across test classes.
 * <p>
 * Usage example:
 * <pre>{@code
 * // Create a default product
 * Product product = ProductTestFactory.defaultProduct();
 *
 * // Create with custom values
 * Product custom = ProductTestFactory.product()
 *         .name("Custom Name")
 *         .price(BigDecimal.TEN)
 *         .build();
 * }</pre>
 */
public final class ProductTestFactory {

	// Default test values
	private static final UUID DEFAULT_ID = UUID.randomUUID();
	private static final String DEFAULT_NAME = "Test Product";
	private static final String DEFAULT_DESCRIPTION = "A test product description";
	private static final BigDecimal DEFAULT_PRICE = new BigDecimal("99.99");
	private static final Integer DEFAULT_STOCK = 100;
	private static final ProductStatus DEFAULT_STATUS = ProductStatus.AVAILABLE;
	private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();
	private static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.now();

	private ProductTestFactory() {
		// Utility class - prevent instantiation
	}

	// ==================== Simple Factory Methods ====================

	/**
	 * Creates a product with all fields populated with default values.
	 *
	 * @return a fully populated product
	 */
	public static Product defaultProduct() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with null values (minimal state).
	 *
	 * @return a product with all null fields
	 */
	public static Product nullProduct() {
		return new Product(
				null, null, null, null, null, null, null, null
		);
	}

	/**
	 * Creates a product without ID and timestamps (for creation scenarios).
	 *
	 * @return a product suitable for creation operations
	 */
	public static Product productForCreation() {
		return new Product(
				null,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				null,
				null
		);
	}

	/**
	 * Creates a product that has been "saved" (with ID and timestamps).
	 *
	 * @return a product with generated fields populated
	 */
	public static Product savedProduct() {
		return savedProduct(DEFAULT_ID);
	}

	/**
	 * Creates a product that has been "saved" with a specific ID.
	 *
	 * @param id the ID to assign
	 * @return a product with generated fields populated
	 */
	public static Product savedProduct(UUID id) {
		return new Product(
				id,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with out-of-stock status.
	 *
	 * @return an out-of-stock product
	 */
	public static Product outOfStockProduct() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				0,
				ProductStatus.OUT_OF_STOCK,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with pre-order status.
	 *
	 * @return a pre-order product
	 */
	public static Product preOrderProduct() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				"Coming soon",
				DEFAULT_PRICE,
				0,
				ProductStatus.PRE_ORDER,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a discontinued product.
	 *
	 * @return a discontinued product
	 */
	public static Product discontinuedProduct() {
		return new Product(
				DEFAULT_ID,
				"Old Product",
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				0,
				ProductStatus.DISCONTINUED,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a free product (zero price).
	 *
	 * @return a free product
	 */
	public static Product freeProduct() {
		return new Product(
				DEFAULT_ID,
				"Free Product",
				DEFAULT_DESCRIPTION,
				BigDecimal.ZERO,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with empty description.
	 *
	 * @return a product with empty description
	 */
	public static Product productWithEmptyDescription() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				"",
				DEFAULT_PRICE,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with negative stock.
	 *
	 * @return a product with negative stock
	 */
	public static Product productWithNegativeStock() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				-5,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with very large price.
	 *
	 * @return a product with large price
	 */
	public static Product productWithLargePrice() {
		return new Product(
				DEFAULT_ID,
				"Premium Product",
				DEFAULT_DESCRIPTION,
				new BigDecimal("999999999999.99"),
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with maximum stock value.
	 *
	 * @return a product with max stock
	 */
	public static Product productWithMaxStock() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				DEFAULT_PRICE,
				Integer.MAX_VALUE,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with negative price.
	 *
	 * @return a product with negative price
	 */
	public static Product productWithNegativePrice() {
		return new Product(
				DEFAULT_ID,
				DEFAULT_NAME,
				DEFAULT_DESCRIPTION,
				new BigDecimal("-10.00"),
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	/**
	 * Creates a product with blank strings.
	 *
	 * @param blankValue the blank string value to use
	 * @return a product with blank name and description
	 */
	public static Product productWithBlankStrings(String blankValue) {
		return new Product(
				DEFAULT_ID,
				blankValue,
				blankValue,
				DEFAULT_PRICE,
				DEFAULT_STOCK,
				DEFAULT_STATUS,
				DEFAULT_CREATED_AT,
				DEFAULT_UPDATED_AT
		);
	}

	// ==================== Builder Pattern Factory ====================

	/**
	 * Creates a new builder for custom product construction.
	 *
	 * @return a new ProductBuilder
	 */
	public static ProductBuilder product() {
		return new ProductBuilder();
	}

	/**
	 * Creates a new builder initialized with default values.
	 *
	 * @return a new ProductBuilder with defaults
	 */
	public static ProductBuilder productWithDefaults() {
		return new ProductBuilder()
				.id(DEFAULT_ID)
				.name(DEFAULT_NAME)
				.description(DEFAULT_DESCRIPTION)
				.price(DEFAULT_PRICE)
				.stock(DEFAULT_STOCK)
				.status(DEFAULT_STATUS)
				.createdAt(DEFAULT_CREATED_AT)
				.updatedAt(DEFAULT_UPDATED_AT);
	}

	/**
	 * Builder for fluent product construction in tests.
	 */
	public static final class ProductBuilder {

		private UUID id = DEFAULT_ID;
		private String name = DEFAULT_NAME;
		private String description = DEFAULT_DESCRIPTION;
		private BigDecimal price = DEFAULT_PRICE;
		private Integer stock = DEFAULT_STOCK;
		private ProductStatus status = DEFAULT_STATUS;
		private LocalDateTime createdAt = DEFAULT_CREATED_AT;
		private LocalDateTime updatedAt = DEFAULT_UPDATED_AT;

		private ProductBuilder() {
		}

		public ProductBuilder id(UUID id) {
			this.id = id;
			return this;
		}

		public ProductBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ProductBuilder description(String description) {
			this.description = description;
			return this;
		}

		public ProductBuilder price(BigDecimal price) {
			this.price = price;
			return this;
		}

		public ProductBuilder stock(Integer stock) {
			this.stock = stock;
			return this;
		}

		public ProductBuilder status(ProductStatus status) {
			this.status = status;
			return this;
		}

		public ProductBuilder createdAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public ProductBuilder updatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		/**
		 * Builds the product with the configured values.
		 *
		 * @return the constructed product
		 */
		public Product build() {
			return new Product(
					id,
					name,
					description,
					price,
					stock,
					status,
					createdAt,
					updatedAt
			);
		}

		/**
		 * Builds a product for creation (null id and timestamps).
		 *
		 * @return the product suitable for creation
		 */
		public Product buildForCreation() {
			return new Product(
					null,
					name,
					description,
					price,
					stock,
					status,
					null,
					null
			);
		}
	}

	// ==================== Value Accessors ====================

	/**
	 * Returns the default ID used for testing.
	 *
	 * @return default test ID
	 */
	public static UUID defaultId() {
		return DEFAULT_ID;
	}

	/**
	 * Returns the default name used for testing.
	 *
	 * @return default test name
	 */
	public static String defaultName() {
		return DEFAULT_NAME;
	}

	/**
	 * Returns the default description used for testing.
	 *
	 * @return default test description
	 */
	public static String defaultDescription() {
		return DEFAULT_DESCRIPTION;
	}

	/**
	 * Returns the default price used for testing.
	 *
	 * @return default test price
	 */
	public static BigDecimal defaultPrice() {
		return DEFAULT_PRICE;
	}

	/**
	 * Returns the default stock used for testing.
	 *
	 * @return default test stock
	 */
	public static Integer defaultStock() {
		return DEFAULT_STOCK;
	}

	/**
	 * Returns the default status used for testing.
	 *
	 * @return default test status
	 */
	public static ProductStatus defaultStatus() {
		return DEFAULT_STATUS;
	}

	/**
	 * Returns the default created-at timestamp used for testing.
	 *
	 * @return default test created-at timestamp
	 */
	public static LocalDateTime defaultCreatedAt() {
		return DEFAULT_CREATED_AT;
	}

	/**
	 * Returns the default updated-at timestamp used for testing.
	 *
	 * @return default test updated-at timestamp
	 */
	public static LocalDateTime defaultUpdatedAt() {
		return DEFAULT_UPDATED_AT;
	}

	/**
	 * Generates a new random UUID for testing.
	 *
	 * @return a new random UUID
	 */
	public static UUID newId() {
		return UUID.randomUUID();
	}

	/**
	 * Generates a new random UUID for testing, different from the default.
	 *
	 * @return a new random UUID different from default
	 */
	public static UUID anotherId() {
		UUID id;
		do {
			id = UUID.randomUUID();
		} while (id.equals(DEFAULT_ID));
		return id;
	}
}
