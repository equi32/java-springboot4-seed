package gov.justucuman.seed.infrastructure.adapter.output.external.dto;

import java.math.BigDecimal;

public record ExternalProductResponse(
        Integer id,
        String title,
        BigDecimal price,
        String description,
        String category
) {
}
