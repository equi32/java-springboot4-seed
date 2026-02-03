package gov.justucuman.seed.infrastructure.adapter.input.rest;

import gov.justucuman.seed.domain.port.in.DeleteProductById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product delete API")
@RequiredArgsConstructor
public class DeleteProductByIdController {

    private final DeleteProductById deleteProductById;

    @Operation(summary = "Deletes a product by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> perform(@PathVariable UUID id) {
        log.info("DELETE /api/v1/products/{}", id);
        deleteProductById.perform(id);
        return ResponseEntity.noContent().build();
    }
}
