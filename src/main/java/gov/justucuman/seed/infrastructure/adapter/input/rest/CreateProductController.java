package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.CreateProduct;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.CreateProductRequest;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.CreateProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product create API")
@RequiredArgsConstructor
public class CreateProductController {

    private final CreateProduct createProduct;

    @Operation(summary = "Creates a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> perform(@Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/v1/products with request {}", request);
        Product createdProduct = createProduct.perform(CreateProductMapper.INSTANCE.toDomain(request));
        ProductResponse response = CreateProductMapper.INSTANCE.toResponse(createdProduct);

        return ResponseEntity.created(URI.create("/api/v1/products/".concat(response.id().toString())))
                .body(response);
    }
}
