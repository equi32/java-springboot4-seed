package gov.justucuman.seed.infrastructure.adapter.output.search;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductSearchPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * No-op implementation of ProductSearchPort for when Elasticsearch is disabled.
 * <p>
 * This implementation is used when {@code elasticsearch.enabled=false} and provides
 * empty results for all search operations.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "false")
public class NoOpProductSearchAdapter implements ProductSearchPort {

	@Override
	public void indexProduct(Product product) {
		log.debug("Elasticsearch disabled - skipping index for product: {}", product.id());
	}

	@Override
	public void deleteProductFromIndex(String productId) {
		log.debug("Elasticsearch disabled - skipping delete from index for product: {}", productId);
	}

	@Override
	public List<Product> search(String query) {
		log.debug("Elasticsearch disabled - returning empty search results for query: {}", query);
		return Collections.emptyList();
	}

	@Override
	public List<Product> searchByParameter(String parameter, String value) {
		log.debug("Elasticsearch disabled - returning empty search results for {}={}", parameter, value);
		return Collections.emptyList();
	}
}
