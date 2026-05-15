package gov.justucuman.seed.unit.infrastructure.adapter.output.persistence.jdbc.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.mapper.ProductRowMapper;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductRowMapper Unit Tests")
class ProductRowMapperTest {

    @Test
    @DisplayName("Should map a fully-populated row to a Product")
    void shouldMapFullyPopulatedRow() throws SQLException {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Timestamp createdAt = Timestamp.valueOf(LocalDateTime.of(2025, 1, 2, 3, 4, 5));
        Timestamp updatedAt = Timestamp.valueOf(LocalDateTime.of(2025, 6, 7, 8, 9, 10));

        ResultSet rs = mock(ResultSet.class);
        given(rs.getString("id")).willReturn(id.toString());
        given(rs.getString("name")).willReturn("Widget");
        given(rs.getString("description")).willReturn("Useful widget");
        given(rs.getBigDecimal("price")).willReturn(new BigDecimal("19.99"));
        given(rs.getInt("stock")).willReturn(42);
        given(rs.getString("status")).willReturn("AVAILABLE");
        given(rs.getTimestamp("created_at")).willReturn(createdAt);
        given(rs.getTimestamp("updated_at")).willReturn(updatedAt);

        Product product = new ProductRowMapper().mapRow(rs, 0);

        assertThat(product.id()).isEqualTo(id);
        assertThat(product.name()).isEqualTo("Widget");
        assertThat(product.description()).isEqualTo("Useful widget");
        assertThat(product.price()).isEqualByComparingTo("19.99");
        assertThat(product.stock()).isEqualTo(42);
        assertThat(product.status()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(product.createdAt()).isEqualTo(createdAt.toLocalDateTime());
        assertThat(product.updatedAt()).isEqualTo(updatedAt.toLocalDateTime());
    }

    @Test
    @DisplayName("Should resolve status case-insensitively")
    void shouldResolveStatusCaseInsensitively() throws SQLException {
        ResultSet rs = baseRow();
        given(rs.getString("status")).willReturn("out_of_stock");

        Product product = new ProductRowMapper().mapRow(rs, 0);

        assertThat(product.status()).isEqualTo(ProductStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("Should leave timestamps null when columns are null")
    void shouldLeaveTimestampsNullWhenColumnsAreNull() throws SQLException {
        ResultSet rs = baseRow();
        given(rs.getTimestamp("created_at")).willReturn(null);
        given(rs.getTimestamp("updated_at")).willReturn(null);

        Product product = new ProductRowMapper().mapRow(rs, 0);

        assertThat(product.createdAt()).isNull();
        assertThat(product.updatedAt()).isNull();
    }

    @Test
    @DisplayName("Should propagate SQLException from the ResultSet")
    void shouldPropagateSqlException() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        given(rs.getString("id")).willThrow(new SQLException("connection closed"));

        assertThatThrownBy(() -> new ProductRowMapper().mapRow(rs, 0))
                .isInstanceOf(SQLException.class)
                .hasMessage("connection closed");
    }

    private static ResultSet baseRow() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        given(rs.getString("id")).willReturn(UUID.randomUUID().toString());
        given(rs.getString("name")).willReturn("Name");
        given(rs.getString("description")).willReturn("Description");
        given(rs.getBigDecimal("price")).willReturn(BigDecimal.ONE);
        given(rs.getInt("stock")).willReturn(1);
        given(rs.getString("status")).willReturn("AVAILABLE");
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        given(rs.getTimestamp("created_at")).willReturn(ts);
        given(rs.getTimestamp("updated_at")).willReturn(ts);
        return rs;
    }
}
