package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.IndexProduct;
import gov.justucuman.seed.domain.port.out.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexProductUseCase implements IndexProduct {

    private final ProductSearchPort productSearchPort;

    @Override
    public void perform(Product product) {
        log.info("Start indexing product {}", product);
        productSearchPort.indexProduct(product);
    }
}
