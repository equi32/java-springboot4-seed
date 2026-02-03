package gov.justucuman.seed.infrastructure.adapter.output.persistence.jdbc;

import gov.justucuman.seed.domain.port.out.ProductDeleteByIdPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDeleteJdbcAdapter implements ProductDeleteByIdPort {

    private final JdbcTemplate jdbcTemplate;

    private static final String DELETE_SQL =
            "DELETE FROM products WHERE id = ?";

    @Override
    public void perform(UUID id) {
        log.info("Deleting product with id {}", id);
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
