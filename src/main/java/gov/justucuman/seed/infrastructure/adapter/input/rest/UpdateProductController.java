package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.port.in.UpdateProduct;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.CreateProductRequest;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.CreateProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product update API")
@RequiredArgsConstructor
public class UpdateProductController {

    private final UpdateProduct updateProduct;

    @Operation(summary = "Updates an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<Void> perform(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request) {
        log.info("PUT /api/v1/products/{} with request {}", id, request);
        updateProduct.perform(CreateProductMapper.INSTANCE.toDomain(request)
                .withId(id));
        return ResponseEntity.noContent().build();
    }
}
