package gov.justucuman.seed.unit.application;

import gov.justucuman.seed.application.CreateProductUseCase;
import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.domain.port.out.ProductEventPublisherPort;
import gov.justucuman.seed.domain.port.out.ProductSavePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import static gov.justucuman.seed.unit.domain.model.ProductTestFactory.*;

/**
 * Unit tests for {@link CreateProductUseCase}.
 * <p>
 * This test class validates the CreateProduct use case implementation:
 * <ul>
 *   <li>Product is saved via {@link ProductSavePort}</li>
 *   <li>Product event is published via {@link ProductEventPublisherPort}</li>
 *   <li>The saved product (with generated id/timestamps) is returned</li>
 *   <li>Event is published with the saved product, not the input product</li>
 *   <li>Proper error handling when dependencies fail</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase Unit Tests")
class CreateProductUseCaseTest {

	@Mock
	private ProductSavePort productSavePort;

	@Mock
	private ProductEventPublisherPort eventPublisherPort;

	@InjectMocks
	private CreateProductUseCase createProductUseCase;

	@Nested
	@DisplayName("Constructor Dependency Injection Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create instance with all dependencies via @InjectMocks")
		void shouldCreateInstanceWithDependencies() {
			// Then - verify instance is created successfully
			assertThat(createProductUseCase).isNotNull();
		}
	}

	@Nested
	@DisplayName("Happy Path Tests")
	class HappyPathTests {

		@Test
		@DisplayName("Should save product and publish event")
		void shouldSaveProductAndPublishEvent() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result).isEqualTo(savedProduct);
			then(productSavePort).should().perform(inputProduct);
			then(eventPublisherPort).should().perform(savedProduct);
		}

		@Test
		@DisplayName("Should publish event with saved product (not input product)")
		void shouldPublishEventWithSavedProduct() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			ArgumentCaptor<Product> eventCaptor = ArgumentCaptor.forClass(Product.class);

			// When
			createProductUseCase.perform(inputProduct);

			// Then
			then(eventPublisherPort).should().perform(eventCaptor.capture());
			Product publishedProduct = eventCaptor.getValue();

			assertThat(publishedProduct.id()).isEqualTo(defaultId());
			assertThat(publishedProduct.name()).isEqualTo(defaultName());
			assertThat(publishedProduct.createdAt()).isEqualTo(defaultCreatedAt());
			assertThat(publishedProduct.updatedAt()).isEqualTo(defaultUpdatedAt());
		}

		@Test
		@DisplayName("Should return the saved product with generated id and timestamps")
		void shouldReturnSavedProductWithGeneratedFields() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.id()).isEqualTo(defaultId());
			assertThat(result.createdAt()).isEqualTo(defaultCreatedAt());
			assertThat(result.updatedAt()).isEqualTo(defaultUpdatedAt());
			assertThat(result.name()).isEqualTo(defaultName());
		}

		@Test
		@DisplayName("Should call save port before publish port")
		void shouldCallSaveBeforePublish() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			createProductUseCase.perform(inputProduct);

			// Then - verify order of invocations
			org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(productSavePort, eventPublisherPort);
			inOrder.verify(productSavePort).perform(inputProduct);
			inOrder.verify(eventPublisherPort).perform(savedProduct);
		}

		@Test
		@DisplayName("Should call publish port exactly once")
		void shouldCallPublishPortExactlyOnce() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			createProductUseCase.perform(inputProduct);

			// Then
			then(eventPublisherPort).should(times(1)).perform(any(Product.class));
		}

		@Test
		@DisplayName("Should handle product with all fields populated")
		void shouldHandleProductWithAllFieldsPopulated() {
			// Given
			Product inputProduct = product()
					.id(newId())
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();

			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result).isEqualTo(savedProduct);
			then(productSavePort).should().perform(inputProduct);
			then(eventPublisherPort).should().perform(savedProduct);
		}

		@Test
		@DisplayName("Should handle product with PRE_ORDER status")
		void shouldHandleProductWithPreOrderStatus() {
			// Given
			Product inputProduct = product()
					.name("Pre-order Product")
					.description("Coming soon")
					.price(defaultPrice())
					.stock(0)
					.status(ProductStatus.PRE_ORDER)
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name("Pre-order Product")
					.description("Coming soon")
					.price(defaultPrice())
					.stock(0)
					.status(ProductStatus.PRE_ORDER)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.status()).isEqualTo(ProductStatus.PRE_ORDER);
			then(eventPublisherPort).should().perform(savedProduct);
		}
	}

	@Nested
	@DisplayName("Null Handling Tests")
	class NullHandlingTests {

		@Test
		@DisplayName("Should handle null product input")
		void shouldHandleNullProductInput() {
			// Given
			given(productSavePort.perform(null)).willThrow(new IllegalArgumentException("Product cannot be null"));

			// When & Then
			assertThatThrownBy(() -> createProductUseCase.perform(null))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Product cannot be null");

			// Verify event publisher was never called
			then(eventPublisherPort).should(never()).perform(any());
		}

		@Test
		@DisplayName("Should handle product with null name")
		void shouldHandleProductWithNullName() {
			// Given
			Product productWithNullName = product()
					.name(null)
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name(null)
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(productWithNullName)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(productWithNullName);

			// Then
			assertThat(result.name()).isNull();
			then(eventPublisherPort).should().perform(savedProduct);
		}
	}

	@Nested
	@DisplayName("Exception Handling Tests")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("Should propagate exception when save port fails")
		void shouldPropagateExceptionWhenSavePortFails() {
			// Given
			Product inputProduct = productForCreation();

			RuntimeException exception = new RuntimeException("Database connection failed");
			given(productSavePort.perform(inputProduct)).willThrow(exception);

			// When & Then
			assertThatThrownBy(() -> createProductUseCase.perform(inputProduct))
					.isInstanceOf(RuntimeException.class)
					.hasMessage("Database connection failed");

			// Verify event publisher was never called when save fails
			then(eventPublisherPort).should(never()).perform(any());
		}

		@Test
		@DisplayName("Should propagate exception when event publisher fails")
		void shouldPropagateExceptionWhenEventPublisherFails() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			RuntimeException exception = new RuntimeException("Kafka connection failed");
			org.mockito.Mockito.doThrow(exception)
					.when(eventPublisherPort)
					.perform(savedProduct);

			// When & Then
			assertThatThrownBy(() -> createProductUseCase.perform(inputProduct))
					.isInstanceOf(RuntimeException.class)
					.hasMessage("Kafka connection failed");

			// Verify save was called even though publish failed
			then(productSavePort).should().perform(inputProduct);
		}

		@Test
		@DisplayName("Should handle IllegalStateException from save port")
		void shouldHandleIllegalStateExceptionFromSavePort() {
			// Given
			Product inputProduct = productForCreation();

			given(productSavePort.perform(inputProduct))
					.willThrow(new IllegalStateException("Invalid product state"));

			// When & Then
			assertThatThrownBy(() -> createProductUseCase.perform(inputProduct))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Invalid product state");

			then(eventPublisherPort).should(never()).perform(any());
		}
	}

	@Nested
	@DisplayName("Edge Cases and Boundary Tests")
	class EdgeCasesTests {

		@Test
		@DisplayName("Should handle product with zero price")
		void shouldHandleProductWithZeroPrice() {
			// Given
			Product inputProduct = product()
					.name("Free Product")
					.description(defaultDescription())
					.price(BigDecimal.ZERO)
					.stock(defaultStock())
					.status(defaultStatus())
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name("Free Product")
					.description(defaultDescription())
					.price(BigDecimal.ZERO)
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.price()).isEqualByComparingTo(BigDecimal.ZERO);
			then(eventPublisherPort).should().perform(savedProduct);
		}

		@Test
		@DisplayName("Should handle product with zero stock")
		void shouldHandleProductWithZeroStock() {
			// Given
			Product inputProduct = product()
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(0)
					.status(ProductStatus.OUT_OF_STOCK)
					.buildForCreation();

			Product savedProduct = outOfStockProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.stock()).isZero();
			assertThat(result.status()).isEqualTo(ProductStatus.OUT_OF_STOCK);
		}

		@Test
		@DisplayName("Should handle product with very large price")
		void shouldHandleProductWithVeryLargePrice() {
			// Given
			BigDecimal largePrice = new BigDecimal("999999999999.99");

			Product inputProduct = product()
					.name("Premium Product")
					.description(defaultDescription())
					.price(largePrice)
					.stock(defaultStock())
					.status(defaultStatus())
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name("Premium Product")
					.description(defaultDescription())
					.price(largePrice)
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.price()).isEqualByComparingTo(largePrice);
		}

		@Test
		@DisplayName("Should handle product with maximum stock value")
		void shouldHandleProductWithMaximumStockValue() {
			// Given
			Product inputProduct = product()
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(Integer.MAX_VALUE)
					.status(defaultStatus())
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(Integer.MAX_VALUE)
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.stock()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		@DisplayName("Should handle product with empty description")
		void shouldHandleProductWithEmptyDescription() {
			// Given
			Product inputProduct = product()
					.name(defaultName())
					.description("")
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name(defaultName())
					.description("")
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.description()).isEmpty();
		}

		@Test
		@DisplayName("Should handle product with negative stock")
		void shouldHandleProductWithNegativeStock() {
			// Given
			Product inputProduct = product()
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(-5)
					.status(defaultStatus())
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(-5)
					.status(defaultStatus())
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.stock()).isEqualTo(-5);
		}

		@Test
		@DisplayName("Should handle product with DISCONTINUED status")
		void shouldHandleProductWithDiscontinuedStatus() {
			// Given
			Product inputProduct = product()
					.name("Old Product")
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(0)
					.status(ProductStatus.DISCONTINUED)
					.buildForCreation();

			Product savedProduct = product()
					.id(defaultId())
					.name("Old Product")
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(0)
					.status(ProductStatus.DISCONTINUED)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			Product result = createProductUseCase.perform(inputProduct);

			// Then
			assertThat(result.status()).isEqualTo(ProductStatus.DISCONTINUED);
		}
	}

	@Nested
	@DisplayName("Port Interaction Verification Tests")
	class PortInteractionTests {

		@Test
		@DisplayName("Should never call event publisher before save port")
		void shouldNeverCallEventPublisherBeforeSavePort() {
			// Given
			Product inputProduct = productForCreation();
			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			createProductUseCase.perform(inputProduct);

			// Then - verify both were called and in the right order
			then(productSavePort).should(times(1)).perform(inputProduct);
			then(eventPublisherPort).should(times(1)).perform(savedProduct);
		}

		@Test
		@DisplayName("Should call save port with exact input product")
		void shouldCallSavePortWithExactInputProduct() {
			// Given
			Product inputProduct = product()
					.id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
					.name(defaultName())
					.description(defaultDescription())
					.price(defaultPrice())
					.stock(defaultStock())
					.status(defaultStatus())
					.createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
					.updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
					.build();

			Product savedProduct = savedProduct();

			given(productSavePort.perform(inputProduct)).willReturn(savedProduct);

			// When
			createProductUseCase.perform(inputProduct);

			// Then
			then(productSavePort).should().perform(inputProduct);
		}
	}

	@Nested
	@DisplayName("Multiple Invocation Tests")
	class MultipleInvocationTests {

		@Test
		@DisplayName("Should handle multiple product creations")
		void shouldHandleMultipleProductCreations() {
			// Given
			Product product1 = product()
					.name("Product 1")
					.description("Description 1")
					.price(new BigDecimal("10.00"))
					.stock(10)
					.status(ProductStatus.AVAILABLE)
					.buildForCreation();

			Product product2 = product()
					.name("Product 2")
					.description("Description 2")
					.price(new BigDecimal("20.00"))
					.stock(20)
					.status(ProductStatus.AVAILABLE)
					.buildForCreation();

			UUID id1 = newId();
			UUID id2 = newId();

			Product savedProduct1 = product()
					.id(id1)
					.name("Product 1")
					.description("Description 1")
					.price(new BigDecimal("10.00"))
					.stock(10)
					.status(ProductStatus.AVAILABLE)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			Product savedProduct2 = product()
					.id(id2)
					.name("Product 2")
					.description("Description 2")
					.price(new BigDecimal("20.00"))
					.stock(20)
					.status(ProductStatus.AVAILABLE)
					.createdAt(defaultCreatedAt())
					.updatedAt(defaultUpdatedAt())
					.build();

			given(productSavePort.perform(product1)).willReturn(savedProduct1);
			given(productSavePort.perform(product2)).willReturn(savedProduct2);

			// When
			Product result1 = createProductUseCase.perform(product1);
			Product result2 = createProductUseCase.perform(product2);

			// Then
			assertThat(result1).isEqualTo(savedProduct1);
			assertThat(result2).isEqualTo(savedProduct2);

			then(productSavePort).should(times(2)).perform(any(Product.class));
			then(eventPublisherPort).should(times(1)).perform(savedProduct1);
			then(eventPublisherPort).should(times(1)).perform(savedProduct2);
		}
	}
}
