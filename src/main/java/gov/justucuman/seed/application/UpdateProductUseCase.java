package gov.justucuman.seed.application;

import gov.justucuman.seed.application.exception.ProductNotFoundException;
import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.UpdateProduct;
import gov.justucuman.seed.domain.port.out.ProductFindByIdPort;
import gov.justucuman.seed.domain.port.out.ProductSavePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase implements UpdateProduct {

    private final ProductFindByIdPort productFindByIdPort;
    private final ProductSavePort productSavePort;

    @Override
    public void perform(Product product) {
        Product productDb = productFindByIdPort.perform(product.id())
                .orElseThrow(() ->
                        new ProductNotFoundException(String.format("Product with id %s was not found", product.id())));
        productSavePort.perform(product);
    }
}
