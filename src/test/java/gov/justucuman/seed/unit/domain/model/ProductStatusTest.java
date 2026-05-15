package gov.justucuman.seed.unit.domain.model;

import gov.justucuman.seed.domain.model.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductStatus Enum Unit Tests")
class ProductStatusTest {

	@Nested
	@DisplayName("fromName Tests")
	class FromNameTests {

		@ParameterizedTest
		@CsvSource({
				"AVAILABLE,     AVAILABLE",
				"OUT_OF_STOCK,  OUT_OF_STOCK",
				"DISCONTINUED,  DISCONTINUED",
				"PRE_ORDER,     PRE_ORDER"
		})
		@DisplayName("Should resolve every status by its exact name")
		void shouldResolveEveryStatusByExactName(String input, ProductStatus expected) {
			assertThat(ProductStatus.fromName(input)).isEqualTo(expected);
		}

		@ParameterizedTest
		@ValueSource(strings = {"available", "Available", "AvAiLaBlE", "AVAILABLE"})
		@DisplayName("Should be case-insensitive")
		void shouldBeCaseInsensitive(String input) {
			assertThat(ProductStatus.fromName(input)).isEqualTo(ProductStatus.AVAILABLE);
		}

		@ParameterizedTest
		@ValueSource(strings = {"UNKNOWN", "available_now", "NOT_A_STATUS", " AVAILABLE"})
		@DisplayName("Should return null when no status matches")
		void shouldReturnNullForUnknownValues(String input) {
			assertThat(ProductStatus.fromName(input)).isNull();
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("Should return null for null or empty input")
		void shouldReturnNullForNullOrEmpty(String input) {
			assertThat(ProductStatus.fromName(input)).isNull();
		}
	}

	@Nested
	@DisplayName("Enum Constants Tests")
	class EnumConstantsTests {

		@Test
		@DisplayName("Should expose exactly four statuses")
		void shouldExposeExactlyFourStatuses() {
			assertThat(ProductStatus.values())
					.hasSize(4)
					.containsExactly(
							ProductStatus.AVAILABLE,
							ProductStatus.OUT_OF_STOCK,
							ProductStatus.DISCONTINUED,
							ProductStatus.PRE_ORDER);
		}

		@Test
		@DisplayName("valueOf should round-trip through name()")
		void valueOfShouldRoundTripThroughName() {
			for (ProductStatus status : ProductStatus.values()) {
				assertThat(ProductStatus.valueOf(status.name())).isEqualTo(status);
			}
		}
	}
}
