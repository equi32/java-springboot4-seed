package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.port.in.GetAllProduct;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.GetProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product getAll API")
@RequiredArgsConstructor
public class GetAllProductController {

    private final GetAllProduct getAllProduct;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> perform() {
        log.info("GET /api/v1/products");
        List<ProductResponse> response = GetProductMapper.INSTANCE.toResponse(getAllProduct.perform());
        return ResponseEntity.ok(response);
    }
}
