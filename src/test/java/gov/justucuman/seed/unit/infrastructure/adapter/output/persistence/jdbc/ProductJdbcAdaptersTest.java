package gov.justucuman.seed.unit.infrastructure.adapter.output.persistence.jdbc;

import static gov.justucuman.seed.unit.domain.model.ProductTestFactory.defaultId;
import static gov.justucuman.seed.unit.domain.model.ProductTestFactory.savedProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.ProductDeleteJdbcAdapter;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.ProductFindAllJdbcAdapter;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.ProductFindByIdJdbcAdapter;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.mapper.ProductRowMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product JDBC Adapters Unit Tests")
class ProductJdbcAdaptersTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ProductFindAllJdbcAdapter findAllAdapter;
    private ProductFindByIdJdbcAdapter findByIdAdapter;
    private ProductDeleteJdbcAdapter deleteAdapter;

    @BeforeEach
    void setUp() {
        findAllAdapter = new ProductFindAllJdbcAdapter(jdbcTemplate);
        findByIdAdapter = new ProductFindByIdJdbcAdapter(jdbcTemplate);
        deleteAdapter = new ProductDeleteJdbcAdapter(jdbcTemplate);
    }

    @Test
    @DisplayName("findAll: should run SELECT ordered by name and return mapped products")
    void findAllShouldRunSelectOrderedByName() {
        Product product = savedProduct();
        given(jdbcTemplate.query(any(String.class), any(ProductRowMapper.class)))
                .willReturn(List.of(product));

        List<Product> result = findAllAdapter.perform();

        assertThat(result).containsExactly(product);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        then(jdbcTemplate).should().query(sqlCaptor.capture(), any(ProductRowMapper.class));
        assertThat(sqlCaptor.getValue()).contains("FROM products").contains("ORDER BY name");
    }

    @Test
    @DisplayName("findAll: should return an empty list when the table has no rows")
    void findAllShouldReturnEmptyListWhenNoRows() {
        given(jdbcTemplate.query(any(String.class), any(ProductRowMapper.class)))
                .willReturn(List.of());

        assertThat(findAllAdapter.perform()).isEmpty();
    }

    @Test
    @DisplayName("findById: should return the product when one row is found")
    void findByIdShouldReturnProductWhenFound() {
        UUID id = defaultId();
        Product product = savedProduct();
        given(jdbcTemplate.queryForObject(any(String.class), any(ProductRowMapper.class), eq(id)))
                .willReturn(product);

        Optional<Product> result = findByIdAdapter.perform(id);

        assertThat(result).contains(product);
    }

    @Test
    @DisplayName("findById: should return empty when no row matches the id")
    void findByIdShouldReturnEmptyWhenNoRowMatches() {
        UUID id = UUID.randomUUID();
        given(jdbcTemplate.queryForObject(any(String.class), any(ProductRowMapper.class), eq(id)))
                .willThrow(new EmptyResultDataAccessException(1));

        Optional<Product> result = findByIdAdapter.perform(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("delete: should issue a DELETE with the given id")
    void deleteShouldIssueDeleteWithGivenId() {
        UUID id = UUID.randomUUID();

        deleteAdapter.perform(id);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        then(jdbcTemplate).should().update(sqlCaptor.capture(), eq(id));
        assertThat(sqlCaptor.getValue()).contains("DELETE FROM products").contains("WHERE id = ?");
    }
}
