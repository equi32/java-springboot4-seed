package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.GetAllProduct;
import gov.justucuman.seed.domain.port.out.ProductFindAllPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllProductUseCase implements GetAllProduct {

    private final ProductFindAllPort productFindAllPort;

    @Override
    public List<Product> perform() {
        return productFindAllPort.perform();
    }
}
