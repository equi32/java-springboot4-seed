package gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductFindAllPort;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc.mapper.ProductRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductFindAllJdbcAdapter implements ProductFindAllPort {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM products ORDER BY name";

    @Override
    public List<Product> perform() {
        log.info("Getting all products");
        return jdbcTemplate.query(SELECT_ALL_SQL, new ProductRowMapper());
    }
}
