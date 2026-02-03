package gov.justucuman.seed.infrastructure.adapter.output.search;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductSearchPort;
import gov.justucuman.seed.infrastructure.adapter.output.search.document.ProductDocument;
import gov.justucuman.seed.infrastructure.adapter.output.search.mapper.ProductDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductElasticsearchAdapter implements ProductSearchPort {

    private final OpenSearchClient openSearchClient;

    @Value("${elasticsearch.index.products}")
    private String indexName;

    @Override
    public void indexProduct(Product product) {
        log.info("Indexing product in OpenSearch: {}", product.id());
        try {
            ProductDocument document = ProductDocumentMapper.INSTANCE.toDocument(product);

            IndexRequest<ProductDocument> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(product.id().toString())
                    .document(document)
            );

            IndexResponse response = openSearchClient.index(request);
            log.debug("Product indexed with result: {}", response.result());

        } catch (IOException e) {
            log.error("Error indexing product in OpenSearch", e);
            throw new RuntimeException("Error indexing product", e);
        }
    }

    @Override
    public void deleteProductFromIndex(String productId) {
        log.info("Deleting product from OpenSearch index: {}", productId);
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(indexName)
                    .id(productId)
            );

            DeleteResponse response = openSearchClient.delete(request);
            log.debug("Product deleted with result: {}", response.result());

        } catch (IOException e) {
            log.error("Error deleting product from OpenSearch", e);
            throw new RuntimeException("Error deleting product from index", e);
        }
    }

    @Override
    public List<Product> search(String query) {
        log.info("Searching products in OpenSearch with query: {} in index {}", query, indexName);
        try {
            // Makes a multiMatch search (search by several fields with ponderation)
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(indexName)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .query(query)
                                    .fields("name^3", "description", "category^2")
                            )
                    )
            );

            SearchResponse<ProductDocument> response = openSearchClient.search(
                    searchRequest,
                    ProductDocument.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(ProductDocumentMapper.INSTANCE::toDomain)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error searching products in OpenSearch", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Product> searchByParameter(String parameter, String value) {
        log.info("Searching products in OpenSearch -> {}={} in index {}", parameter, value, indexName);
        try {
            // Makes a search by a particular String value
            SearchResponse<ProductDocument> response = openSearchClient.search(s -> s
                    .index(indexName)
                    .query(Query.of(q -> q
                            .match(t -> t
                                    .field(parameter)
                                    .query(v -> v.stringValue(value))
                            )
                    )), ProductDocument.class);

            log.info("Response from OpenSearch: {}", response.hits().hits().size());

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(ProductDocumentMapper.INSTANCE::toDomain)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error searching products in OpenSearch", e);
            return new ArrayList<>();
        }
    }
}
