package gov.justucuman.seed.infrastructure.adapter.output.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductEvent(
        String type,
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String status,
        LocalDateTime createdAt
) {
}
