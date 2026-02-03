package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.GetExternalProductById;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.GetProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/products/external")
@Tag(name = "External Products", description = "External Product gets API")
@RequiredArgsConstructor
public class GetExternalProductByIdController {

    private final GetExternalProductById getExternalProductById;

    @Operation(summary = "Get an External Product by Id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> perform(@PathVariable Integer id) {
        log.info("GET /api/v1/products/external/{}", id);
        Product product = getExternalProductById.perform(id);
        return ResponseEntity.ok(GetProductMapper.INSTANCE.toResponse(product));
    }
}
