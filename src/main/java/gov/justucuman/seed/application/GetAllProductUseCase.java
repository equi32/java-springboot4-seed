package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.GetAllProduct;
import gov.justucuman.seed.domain.port.out.ProductFindAllPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllProductUseCase implements GetAllProduct {

    private final ProductFindAllPort productFindAllPort;

    @Override
    public List<Product> perform() {
        return productFindAllPort.perform();
    }
}
