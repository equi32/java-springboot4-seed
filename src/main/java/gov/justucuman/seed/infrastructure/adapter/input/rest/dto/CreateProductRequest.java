package gov.justucuman.seed.infrastructure.adapter.input.rest.dto;

import gov.justucuman.seed.domain.model.ProductStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotNull String name,
        String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        @NotNull ProductStatus status
) {
}
