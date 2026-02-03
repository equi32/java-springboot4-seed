package gov.justucuman.seed.application;

import gov.justucuman.seed.application.exception.ProductNotFoundException;
import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.DeleteProductById;
import gov.justucuman.seed.domain.port.out.ProductDeleteByIdPort;
import gov.justucuman.seed.domain.port.out.ProductFindByIdPort;
import gov.justucuman.seed.domain.port.out.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductByIdUseCase implements DeleteProductById {

    private final ProductFindByIdPort productFindByIdPort;
    private final ProductDeleteByIdPort productDeleteByIdPort;
    private final ProductSearchPort productSearchPort;

    @Override
    public void perform(UUID id) {
        Product product = productFindByIdPort.perform(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(String.format("Product with id %s was not found", id)));
        productDeleteByIdPort.perform(product.id());
        productSearchPort.deleteProductFromIndex(product.id().toString());
    }
}
