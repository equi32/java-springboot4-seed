package gov.justucuman.seed.integration.karate;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.domain.port.in.IndexProduct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.context.ApplicationContext;

public final class KarateBridge {

    private static ApplicationContext context;
    private static String indexName;

    private KarateBridge() {}

    public static void initialize(ApplicationContext applicationContext, String elasticsearchIndex) {
        context = applicationContext;
        indexName = elasticsearchIndex;
    }

    public static void indexAndRefresh(Map<String, Object> productJson) {
        if (context == null) {
            throw new IllegalStateException("KarateBridge not initialized - call initialize() first");
        }
        Product product = new Product(
                UUID.fromString((String) productJson.get("id")),
                (String) productJson.get("name"),
                (String) productJson.get("description"),
                new BigDecimal(productJson.get("price").toString()),
                ((Number) productJson.get("stock")).intValue(),
                ProductStatus.valueOf((String) productJson.get("status")),
                null,
                null);
        context.getBean(IndexProduct.class).perform(product);
        try {
            context.getBean(OpenSearchClient.class).indices().refresh(r -> r.index(indexName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to refresh OpenSearch index", e);
        }
    }
}
