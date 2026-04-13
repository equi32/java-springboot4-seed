package gov.justucuman.seed.unit.domain.model;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static gov.justucuman.seed.unit.domain.model.ProductTestFactory.*;

/**
 * Unit tests for {@link Product} record.
 * <p>
 * This test class validates:
 * <ul>
 *   <li>Record constructor and field access</li>
 *   <li>Lombok {@code @With} annotation generated method (for id field only)</li>
 *   <li>{@code equals()}, {@code hashCode()}, and {@code toString()} behavior</li>
 *   <li>Edge cases and null handling</li>
 * </ul>
 */
@DisplayName("Product Record Unit Tests")
class ProductTest {

	@Nested
	@DisplayName("Constructor and Accessor Tests")
	class ConstructorAndAccessorTests {

		@Test
		@DisplayName("Should create product with all fields")
		void shouldCreateProductWithAllFields() {
			// When
			Product product = defaultProduct();

			// Then
			assertThat(product.id()).isEqualTo(defaultId());
			assertThat(product.name()).isEqualTo(defaultName());
			assertThat(product.description()).isEqualTo(defaultDescription());
			assertThat(product.price()).isEqualByComparingTo(defaultPrice());
			assertThat(product.stock()).isEqualTo(defaultStock());
			assertThat(product.status()).isEqualTo(defaultStatus());
			assertThat(product.createdAt()).isEqualTo(defaultCreatedAt());
			assertThat(product.updatedAt()).isEqualTo(defaultUpdatedAt());
		}

		@Test
		@DisplayName("Should create product with null values")
		void shouldCreateProductWithNullValues() {
			// When
			Product product = nullProduct();

			// Then
			assertThat(product.id()).isNull();
			assertThat(product.name()).isNull();
			assertThat(product.description()).isNull();
			assertThat(product.price()).isNull();
			assertThat(product.stock()).isNull();
			assertThat(product.status()).isNull();
			assertThat(product.createdAt()).isNull();
			assertThat(product.updatedAt()).isNull();
		}

		@Test
		@DisplayName("Should handle zero stock and zero price")
		void shouldHandleZeroStockAndZeroPrice() {
			// When
			Product product = product()
					.price(BigDecimal.ZERO)
					.stock(0)
					.status(ProductStatus.OUT_OF_STOCK)
					.build();

			// Then
			assertThat(product.price()).isEqualByComparingTo(BigDecimal.ZERO);
			assertThat(product.stock()).isZero();
			assertThat(product.status()).isEqualTo(ProductStatus.OUT_OF_STOCK);
		}
	}

	@Nested
	@DisplayName("Lombok @With Annotation Tests (id field only)")
	class WithAnnotationTests {

		@Test
		@DisplayName("Should create new product with updated id using withId()")
		void shouldCreateNewProductWithUpdatedId() {
			// Given
			Product original = defaultProduct();
			UUID newId = anotherId();

			// When
			Product updated = original.withId(newId);

			// Then
			assertThat(updated.id()).isEqualTo(newId);
			assertThat(updated.name()).isEqualTo(original.name());
			assertThat(updated.description()).isEqualTo(original.description());
			assertThat(updated.price()).isEqualByComparingTo(original.price());
			assertThat(updated.stock()).isEqualTo(original.stock());
			assertThat(updated.status()).isEqualTo(original.status());
			assertThat(updated.createdAt()).isEqualTo(original.createdAt());
			assertThat(updated.updatedAt()).isEqualTo(original.updatedAt());
		}

		@Test
		@DisplayName("Should preserve original instance immutability when using withId()")
		void shouldPreserveOriginalImmutability() {
			// Given
			Product original = defaultProduct();

			// When
			original.withId(newId());

			// Then - original should be unchanged
			assertThat(original.id()).isEqualTo(defaultId());
		}

		@ParameterizedTest
		@NullSource
		@DisplayName("Should handle null value in withId method")
		void shouldHandleNullValueInWithIdMethod(UUID nullValue) {
			// Given
			Product original = defaultProduct();

			// When
			Product updated = original.withId(nullValue);

			// Then
			assertThat(updated.id()).isNull();
			assertThat(updated.name()).isEqualTo(original.name());
		}

		@Test
		@DisplayName("Should create independent copies when using withId()")
		void shouldCreateIndependentCopiesWhenUsingWithId() {
			// Given
			Product original = defaultProduct();
			UUID newId = anotherId();

			// When
			Product copy = original.withId(newId);

			// Then
			assertThat(original.id()).isNotEqualTo(copy.id());
			assertThat(original.name()).isEqualTo(copy.name());
			assertThat(original.description()).isEqualTo(copy.description());
		}
	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Should be equal when all fields match")
		void shouldBeEqualWhenAllFieldsMatch() {
			// Given
			Product product1 = defaultProduct();
			Product product2 = defaultProduct();

			// Then
			assertThat(product1).isEqualTo(product2);
			assertThat(product2).isEqualTo(product1);
		}

		@Test
		@DisplayName("Should have same hashCode when equal")
		void shouldHaveSameHashCodeWhenEqual() {
			// Given
			Product product1 = defaultProduct();
			Product product2 = defaultProduct();

			// Then
			assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
		}

		@Test
		@DisplayName("Should not be equal when id differs")
		void shouldNotBeEqualWhenIdDiffers() {
			// Given
			Product product1 = defaultProduct();
			Product product2 = defaultProduct().withId(anotherId());

			// Then
			assertThat(product1).isNotEqualTo(product2);
		}

		@Test
		@DisplayName("Should not be equal when name differs")
		void shouldNotBeEqualWhenNameDiffers() {
			// Given
			Product product1 = defaultProduct();
			Product product2 = product()
					.id(defaultId())
					.name("Different Name")
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			// Then
			assertThat(product1).isNotEqualTo(product2);
		}

		@Test
		@DisplayName("Should not be equal when price differs")
		void shouldNotBeEqualWhenPriceDiffers() {
			// Given
			Product product1 = defaultProduct();
			Product product2 = product()
					.id(defaultId())
					.name(defaultName())
					.description(defaultDescription())
					.price(new BigDecimal("199.99"))
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			// Then
			assertThat(product1).isNotEqualTo(product2);
		}

		@Test
		@DisplayName("Should not be equal when status differs")
		void shouldNotBeEqualWhenStatusDiffers() {
			// Given
			Product product1 = product()
					.id(defaultId())
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(ProductStatus.AVAILABLE)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			Product product2 = product()
					.id(defaultId())
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(ProductStatus.DISCONTINUED)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			// Then
			assertThat(product1).isNotEqualTo(product2);
		}

		@Test
		@DisplayName("Should not be equal to null")
		void shouldNotBeEqualToNull() {
			// Given
			Product product = defaultProduct();

			// Then
			assertThat(product).isNotEqualTo(null);
		}

		@Test
		@DisplayName("Should be equal to itself")
		void shouldBeEqualToItself() {
			// Given
			Product product = defaultProduct();

			// Then
			assertThat(product).isEqualTo(product);
		}

		@Test
		@DisplayName("Should not be equal to different type")
		void shouldNotBeEqualToDifferentType() {
			// Given
			Product product = defaultProduct();

			// Then
			assertThat(product).isNotEqualTo("Not a Product");
			assertThat(product).isNotEqualTo(defaultId());
		}
	}

	@Nested
	@DisplayName("ToString Tests")
	class ToStringTests {

		@Test
		@DisplayName("Should generate toString with all fields")
		void shouldGenerateToStringWithAllFields() {
			// Given
			Product product = defaultProduct();

			// When
			String toString = product.toString();

			// Then
			assertThat(toString).contains("Product");
			assertThat(toString).contains(defaultId().toString());
			assertThat(toString).contains(defaultName());
			assertThat(toString).contains(defaultDescription());
			assertThat(toString).contains(defaultPrice().toString());
			assertThat(toString).contains(defaultStock().toString());
			assertThat(toString).contains(defaultStatus().toString());
		}

		@Test
		@DisplayName("Should generate toString with null fields")
		void shouldGenerateToStringWithNullFields() {
			// Given
			Product product = nullProduct();

			// When
			String toString = product.toString();

			// Then
			assertThat(toString).contains("Product");
		}
	}

	@Nested
	@DisplayName("Record Immutability Tests")
	class RecordImmutabilityTests {

		@Test
		@DisplayName("Should be immutable - record cannot be modified after creation")
		void shouldBeImmutable() {
			// Given
			Product original = defaultProduct();

			// When - create a new instance with withId()
			Product modified = original.withId(anotherId());

			// Then - original is unchanged
			assertThat(original.id()).isEqualTo(defaultId());
			assertThat(modified.id()).isNotEqualTo(defaultId());
		}

		@Test
		@DisplayName("Should create new instance for each modification via withId()")
		void shouldCreateNewInstanceForEachModification() {
			// Given
			Product original = defaultProduct();

			// When
			UUID newId1 = anotherId();
			UUID newId2 = newId();
			Product copy1 = original.withId(newId1);
			Product copy2 = copy1.withId(newId2);

			// Then - all three instances are independent
			assertThat(original.id()).isEqualTo(defaultId());
			assertThat(copy1.id()).isEqualTo(newId1);
			assertThat(copy2.id()).isEqualTo(newId2);
		}
	}

	@Nested
	@DisplayName("Edge Cases and Boundary Tests")
	class EdgeCasesTests {

		@Test
		@DisplayName("Should handle negative price")
		void shouldHandleNegativePrice() {
			// Given
			Product product = productWithNegativePrice();

			// Then
			assertThat(product.price()).isEqualByComparingTo("-10.00");
		}

		@Test
		@DisplayName("Should handle negative stock")
		void shouldHandleNegativeStock() {
			// When
			Product product = productWithNegativeStock();

			// Then
			assertThat(product.stock()).isEqualTo(-5);
		}

		@Test
		@DisplayName("Should handle very large price values")
		void shouldHandleVeryLargePriceValues() {
			// When
			Product product = productWithLargePrice();

			// Then
			assertThat(product.price()).isEqualByComparingTo("999999999999.99");
		}

		@Test
		@DisplayName("Should handle very high stock values")
		void shouldHandleVeryHighStockValues() {
			// When
			Product product = productWithMaxStock();

			// Then
			assertThat(product.stock()).isEqualTo(Integer.MAX_VALUE);
		}

		@ParameterizedTest
		@ValueSource(strings = {"", " ", "\t", "\n"})
		@DisplayName("Should handle blank strings for name and description")
		void shouldHandleBlankStrings(String blankValue) {
			// When
			Product product = productWithBlankStrings(blankValue);

			// Then
			assertThat(product.name()).isEqualTo(blankValue);
			assertThat(product.description()).isEqualTo(blankValue);
		}

		@Test
		@DisplayName("Should handle all ProductStatus values")
		void shouldHandleAllProductStatusValues() {
			// When & Then - test all enum values
			Product available = product()
					.status(ProductStatus.AVAILABLE)
					.build();
			assertThat(available.status()).isEqualTo(ProductStatus.AVAILABLE);

			Product outOfStock = outOfStockProduct();
			assertThat(outOfStock.status()).isEqualTo(ProductStatus.OUT_OF_STOCK);

			Product discontinued = discontinuedProduct();
			assertThat(discontinued.status()).isEqualTo(ProductStatus.DISCONTINUED);

			Product preOrder = preOrderProduct();
			assertThat(preOrder.status()).isEqualTo(ProductStatus.PRE_ORDER);
		}

		@Test
		@DisplayName("Should handle BigDecimal scale differences")
		void shouldHandleBigDecimalScaleDifferences() {
			// Given
			BigDecimal price1 = new BigDecimal("99.99");
			BigDecimal price2 = new BigDecimal("99.990");
			BigDecimal price3 = new BigDecimal("100.00");

			// When
			Product product1 = product()
					.price(price1)
					.build();

			// Then - same value, different scale should be equal
			assertThat(product1.price()).isEqualByComparingTo(price2);

			// When - different value
			Product product2 = product()
					.id(newId())
					.price(price3)
					.build();

			// Then
			assertThat(product2.price()).isEqualByComparingTo(price3);
		}

		@Test
		@DisplayName("Should handle LocalDateTime with nanoseconds")
		void shouldHandleLocalDateTimeWithNanoseconds() {
			// Given
			LocalDateTime timeWithNanos = defaultCreatedAt().withNano(123456789);

			// When
			Product product = product()
					.createdAt(timeWithNanos)
					.build();

			// Then
			assertThat(product.createdAt()).isEqualTo(timeWithNanos);
			assertThat(product.createdAt().getNano()).isEqualTo(123456789);
		}
	}
}
