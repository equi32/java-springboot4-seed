package gov.justucuman.seed.unit.infrastructure.adapter.output.search;

import static gov.justucuman.seed.unit.domain.model.ProductTestFactory.savedProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.infrastructure.adapter.output.search.ProductElasticsearchAdapter;
import gov.justucuman.seed.infrastructure.adapter.output.search.document.ProductDocument;
import gov.justucuman.seed.infrastructure.adapter.output.search.mapper.ProductDocumentMapper;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductElasticsearchAdapter Unit Tests")
class ProductElasticsearchAdapterTest {

    private static final String INDEX = "products";

    @Mock
    private OpenSearchClient openSearchClient;

    private ProductElasticsearchAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductElasticsearchAdapter(openSearchClient);
        ReflectionTestUtils.setField(adapter, "indexName", INDEX);
    }

    @Nested
    @DisplayName("indexProduct")
    class IndexProductTests {

        @Test
        @DisplayName("Should send the document under the product id to the configured index")
        void shouldSendDocumentUnderProductId() throws IOException {
            Product product = savedProduct();
            given(openSearchClient.index(any(IndexRequest.class))).willReturn(mock(IndexResponse.class));

            adapter.indexProduct(product);

            ArgumentCaptor<IndexRequest<ProductDocument>> captor = captor();
            org.mockito.BDDMockito.then(openSearchClient).should().index(captor.capture());
            IndexRequest<ProductDocument> request = captor.getValue();
            assertThat(request.index()).isEqualTo(INDEX);
            assertThat(request.id()).isEqualTo(product.id().toString());
            assertThat(request.document().id()).isEqualTo(product.id());
        }

        @Test
        @DisplayName("Should wrap IOException as RuntimeException")
        void shouldWrapIoException() throws IOException {
            given(openSearchClient.index(any(IndexRequest.class))).willThrow(new IOException("boom"));

            assertThatThrownBy(() -> adapter.indexProduct(savedProduct()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error indexing product");
        }
    }

    @Nested
    @DisplayName("deleteProductFromIndex")
    class DeleteFromIndexTests {

        @Test
        @DisplayName("Should issue a delete against the configured index")
        void shouldIssueDeleteAgainstConfiguredIndex() throws IOException {
            given(openSearchClient.delete(any(DeleteRequest.class))).willReturn(mock(DeleteResponse.class));

            adapter.deleteProductFromIndex("abc-123");

            ArgumentCaptor<DeleteRequest> captor = ArgumentCaptor.forClass(DeleteRequest.class);
            org.mockito.BDDMockito.then(openSearchClient).should().delete(captor.capture());
            assertThat(captor.getValue().index()).isEqualTo(INDEX);
            assertThat(captor.getValue().id()).isEqualTo("abc-123");
        }

        @Test
        @DisplayName("Should wrap IOException as RuntimeException")
        void shouldWrapIoException() throws IOException {
            given(openSearchClient.delete(any(DeleteRequest.class))).willThrow(new IOException("boom"));

            assertThatThrownBy(() -> adapter.deleteProductFromIndex("abc-123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error deleting product from index");
        }
    }

    @Nested
    @DisplayName("search")
    class SearchTests {

        @Test
        @DisplayName("Should map hits to domain products")
        void shouldMapHitsToDomainProducts() throws IOException {
            Product product = savedProduct();
            SearchResponse<ProductDocument> response = stubSearchResponseWith(product);
            given(openSearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
                    .willReturn(response);

            List<Product> result = adapter.search("widget");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(product.id());
            assertThat(result.get(0).name()).isEqualTo(product.name());
        }

        @Test
        @DisplayName("Should return an empty list when IOException occurs")
        void shouldReturnEmptyListOnIoException() throws IOException {
            given(openSearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
                    .willThrow(new IOException("boom"));

            assertThat(adapter.search("widget")).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchByParameter")
    class SearchByParameterTests {

        @Test
        @DisplayName("Should map hits to domain products")
        @SuppressWarnings("unchecked")
        void shouldMapHitsToDomainProducts() throws IOException {
            Product product = savedProduct();
            SearchResponse<ProductDocument> response = stubSearchResponseWith(product);
            given(openSearchClient.search(any(Function.class), eq(ProductDocument.class)))
                    .willReturn(response);

            List<Product> result = adapter.searchByParameter("name", "widget");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(product.id());
        }

        @Test
        @DisplayName("Should return an empty list when IOException occurs")
        @SuppressWarnings("unchecked")
        void shouldReturnEmptyListOnIoException() throws IOException {
            given(openSearchClient.search(any(Function.class), eq(ProductDocument.class)))
                    .willThrow(new IOException("boom"));

            assertThat(adapter.searchByParameter("name", "widget")).isEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    private static SearchResponse<ProductDocument> stubSearchResponseWith(Product product) {
        ProductDocument document = ProductDocumentMapper.INSTANCE.toDocument(product);
        Hit<ProductDocument> hit = mock(Hit.class);
        given(hit.source()).willReturn(document);
        HitsMetadata<ProductDocument> hits = mock(HitsMetadata.class);
        given(hits.hits()).willReturn(List.of(hit));
        SearchResponse<ProductDocument> response = mock(SearchResponse.class);
        given(response.hits()).willReturn(hits);
        return response;
    }

    @SuppressWarnings("unchecked")
    private static <T> ArgumentCaptor<IndexRequest<T>> captor() {
        return ArgumentCaptor.forClass(IndexRequest.class);
    }
}
