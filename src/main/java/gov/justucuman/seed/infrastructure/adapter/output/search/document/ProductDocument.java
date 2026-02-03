package gov.justucuman.seed.infrastructure.adapter.output.search.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDocument(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String status,
        LocalDateTime createdAt
) {
}
