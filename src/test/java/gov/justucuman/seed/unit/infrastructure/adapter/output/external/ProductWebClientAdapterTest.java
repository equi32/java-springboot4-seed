package gov.justucuman.seed.unit.infrastructure.adapter.output.external;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.infrastructure.adapter.output.external.ProductWebClientAdapter;
import gov.justucuman.seed.infrastructure.adapter.output.external.exception.ExternalApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductWebClientAdapter Unit Tests")
class ProductWebClientAdapterTest {

	private static final String BASE_URL = "http://external.test";

	private ProductWebClientAdapter buildAdapter(ExchangeFunction exchange) {
		WebClient webClient = WebClient.builder()
				.baseUrl(BASE_URL)
				.exchangeFunction(exchange)
				.build();
		ProductWebClientAdapter adapter = new ProductWebClientAdapter(webClient);
		ReflectionTestUtils.setField(adapter, "baseUrl", BASE_URL);
		ReflectionTestUtils.setField(adapter, "retryAttempts", 0);
		return adapter;
	}

	@Nested
	@DisplayName("getById")
	class GetByIdTests {

		@Test
		@DisplayName("Should call GET /products/{id} and map the JSON body to a Product")
		void shouldCallGetByIdAndMapBody() {
			AtomicReference<String> capturedPath = new AtomicReference<>();
			ProductWebClientAdapter adapter = buildAdapter(req -> {
				capturedPath.set(req.url().getPath());
				return Mono.just(ClientResponse.create(HttpStatus.OK)
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.body("""
								{
									"id": 7,
									"title": "Hat",
									"price": 12.50,
									"description": "Felt hat",
									"category": "apparel"
								}
								""")
						.build());
			});

			Product product = adapter.getById(7);

			assertThat(capturedPath.get()).isEqualTo("/products/7");
			assertThat(product.name()).isEqualTo("Hat");
			assertThat(product.description()).isEqualTo("Felt hat");
			assertThat(product.price()).isEqualByComparingTo("12.50");
			// Mapper applies fixed defaults for fields not present in the external response
			assertThat(product.stock()).isEqualTo(1);
			assertThat(product.status()).isEqualTo(gov.justucuman.seed.domain.model.ProductStatus.OUT_OF_STOCK);
			assertThat(product.createdAt()).isNotNull();
		}

		@Test
		@DisplayName("Should throw ExternalApiException when the response has no body")
		void shouldThrowWhenBodyIsEmpty() {
			ProductWebClientAdapter adapter = buildAdapter(req -> Mono.just(
					ClientResponse.create(HttpStatus.NO_CONTENT).build()));

			assertThatThrownBy(() -> adapter.getById(7))
					.isInstanceOf(ExternalApiException.class)
					.hasMessageContaining("Empty");
		}

		@Test
		@DisplayName("Should surface a RuntimeException when the upstream returns an error status")
		void shouldSurfaceRuntimeExceptionOnErrorStatus() {
			ProductWebClientAdapter adapter = buildAdapter(req -> Mono.just(
					ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
							.header("Content-Type", MediaType.TEXT_PLAIN_VALUE)
							.body("upstream exploded")
							.build()));

			assertThatThrownBy(() -> adapter.getById(7))
					.isInstanceOf(RuntimeException.class);
		}
	}

	@Nested
	@DisplayName("getAll")
	class GetAllTests {

		@Test
		@DisplayName("Should call GET /products and map the JSON array to Products")
		void shouldCallGetAllAndMapBody() {
			AtomicReference<String> capturedPath = new AtomicReference<>();
			ProductWebClientAdapter adapter = buildAdapter(req -> {
				capturedPath.set(req.url().getPath());
				return Mono.just(ClientResponse.create(HttpStatus.OK)
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.body("""
								[
									{"id": 1, "title": "A", "price": 1.00, "description": "d1", "category": "c"},
									{"id": 2, "title": "B", "price": 2.00, "description": "d2", "category": "c"}
								]
								""")
						.build());
			});

			List<Product> products = adapter.getAll();

			assertThat(capturedPath.get()).isEqualTo("/products");
			assertThat(products).hasSize(2);
			assertThat(products).extracting(Product::name).containsExactly("A", "B");
		}

		@Test
		@DisplayName("Should return an empty list when the external API returns []")
		void shouldReturnEmptyListWhenArrayIsEmpty() {
			ProductWebClientAdapter adapter = buildAdapter(req -> Mono.just(
					ClientResponse.create(HttpStatus.OK)
							.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
							.body("[]")
							.build()));

			assertThat(adapter.getAll()).isEmpty();
		}

		@Test
		@DisplayName("Should surface a RuntimeException when the upstream returns an error status")
		void shouldSurfaceRuntimeExceptionOnErrorStatus() {
			ProductWebClientAdapter adapter = buildAdapter(req -> Mono.just(
					ClientResponse.create(HttpStatus.BAD_GATEWAY)
							.header("Content-Type", MediaType.TEXT_PLAIN_VALUE)
							.body("gateway down")
							.build()));

			assertThatThrownBy(adapter::getAll).isInstanceOf(RuntimeException.class);
		}
	}
}
