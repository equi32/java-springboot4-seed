package gov.justucuman.seed.infrastructure.adapter.input.rest.dto;

import gov.justucuman.seed.domain.model.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank
        @Size(max = 100)
        String name,
        @Size(max = 1000)
        String description,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,
        @NotNull
        @PositiveOrZero
        Integer stock,
        @NotNull ProductStatus status
) {
}
