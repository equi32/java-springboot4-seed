package gov.justucuman.seed.infrastructure.adapter.output.search.document;

import com.fasterxml.jackson.annotation.JsonFormat;

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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
}
