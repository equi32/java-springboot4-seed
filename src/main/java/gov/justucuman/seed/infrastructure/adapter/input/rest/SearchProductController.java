package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.port.in.SearchProduct;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.GetProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product getAll API")
@RequiredArgsConstructor
public class SearchProductController {

    private final SearchProduct searchProduct;

    @Operation(summary = "Search products")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> perform(@RequestParam String term, @RequestParam String value) {
        log.info("GET /api/v1/products/search with term {}={}", term, value);
        List<ProductResponse> response = GetProductMapper.INSTANCE.toResponse(searchProduct.perform(term, value));
        return ResponseEntity.ok(response);
    }
}
