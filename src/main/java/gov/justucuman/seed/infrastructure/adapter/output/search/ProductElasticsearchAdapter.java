package gov.justucuman.seed.infrastructure.adapter.output.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
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

    private final ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.products}")
    private String indexName;

    @Override
    public void indexProduct(Product product) {
        log.info("Indexing product in Elasticsearch: {}", product.id());
        try {
            ProductDocument document = ProductDocumentMapper.INSTANCE.toDocument(product);

            IndexRequest<ProductDocument> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(product.id().toString())
                    .document(document)
            );

            IndexResponse response = elasticsearchClient.index(request);
            log.debug("Product indexed with result: {}", response.result());

        } catch (IOException e) {
            log.error("Error indexing product in Elasticsearch", e);
            throw new RuntimeException("Error indexing product", e);
        }
    }

    @Override
    public void deleteProductFromIndex(String productId) {
        log.info("Deleting product from Elasticsearch index: {}", productId);
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(indexName)
                    .id(productId)
            );

            DeleteResponse response = elasticsearchClient.delete(request);
            log.debug("Product deleted with result: {}", response.result());

        } catch (IOException e) {
            log.error("Error deleting product from Elasticsearch", e);
            throw new RuntimeException("Error deleting product from index", e);
        }
    }

    @Override
    public List<Product> search(String query) {
        log.info("Searching products in Elasticsearch with query: {} in index {}", query, indexName);
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

            SearchResponse<ProductDocument> response = elasticsearchClient.search(
                    searchRequest,
                    ProductDocument.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(ProductDocumentMapper.INSTANCE::toDomain)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error searching products in Elasticsearch", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Product> searchByParameter(String parameter, String value) {
        log.info("Searching products in Elasticsearch -> {}={} in index {}", parameter, value, indexName);
        try {
            // Makes a search by a particular String value
            SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                    .index(indexName)
                    .query(Query.of(q -> q
                            .term(t -> t
                                    .field(parameter)
                                    .value(v -> v.stringValue(value))
                            )
                    )), ProductDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(ProductDocumentMapper.INSTANCE::toDomain)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error searching products in Elasticsearch", e);
            return new ArrayList<>();
        }
    }
}
