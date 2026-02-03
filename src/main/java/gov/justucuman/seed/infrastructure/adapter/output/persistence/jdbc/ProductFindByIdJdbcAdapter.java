package gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductFindByIdPort;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.mapper.ProductRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductFindByIdJdbcAdapter implements ProductFindByIdPort {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM products WHERE id = ?";

    @Override
    public Optional<Product> perform(UUID id) {
        log.info("Searching product with id {}", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new ProductRowMapper(), id));
        } catch (EmptyResultDataAccessException ex) {
            log.info("Product with id {} was not found: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }
}
