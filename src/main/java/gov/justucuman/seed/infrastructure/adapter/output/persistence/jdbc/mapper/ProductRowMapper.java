package gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.mapper;

import gov.justucuman.seed.common.util.DateUtils;
import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                ProductStatus.fromName(rs.getString("status")),
                DateUtils.toLocalDateTime(rs.getTimestamp("created_at")),
                DateUtils.toLocalDateTime(rs.getTimestamp("updated_at"))
        );
    }
}
