package gov.justucuman.seed.infrastructure.adapter.output.external;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ExternalProductPort;
import gov.justucuman.seed.infrastructure.adapter.output.external.dto.ExternalProductResponse;
import gov.justucuman.seed.infrastructure.adapter.output.external.exception.ExternalApiException;
import gov.justucuman.seed.infrastructure.adapter.output.external.mapper.ExternalProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWebClientAdapter implements ExternalProductPort {

    private final WebClient productWebClient;

    @Value("${external.api.product.base-url}")
    private String baseUrl;

    @Value("${external.api.product.retry-attempts:3}")
    private int retryAttempts;

    private static final String PRODUCT_PATH = "/products";
    private static final String PRODUCT_BY_ID_PATH = "/products/{productId}";

    @Override
    public Product getById(Integer id) {
        log.info("Searching for external Product with ID: {} in GET {}", id, baseUrl.concat(PRODUCT_BY_ID_PATH));

        Optional<ExternalProductResponse> response = Optional.ofNullable(productWebClient
                .get()
                .uri(PRODUCT_BY_ID_PATH, id.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrors)
                .bodyToMono(ExternalProductResponse.class)
                .block());

        if (response.isEmpty()) {
            throw new ExternalApiException("The response body is Empty");
        }

        return ExternalProductMapper.INSTANCE.toDomain(response.get());
    }

    @Override
    public List<Product> getAll() {
        log.info("Start searching ALL products: GET {}", baseUrl.concat(PRODUCT_PATH));
        return productWebClient
                .get()
                .uri(PRODUCT_PATH)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrors)
                .bodyToFlux(ExternalProductResponse.class)
                .map(ExternalProductMapper.INSTANCE::toDomain)
                .collectList()
                .block();
    }

    private Mono<? extends Throwable> handleErrors(ClientResponse response) {
        log.info("Error {} from external client {}", response.statusCode(), response.bodyToMono(String.class).block());
        return response
                .bodyToMono(String.class)
                .defaultIfEmpty("Empty body")
                .flatMap(error -> Mono.error(new RuntimeException(error)));
    }
}
