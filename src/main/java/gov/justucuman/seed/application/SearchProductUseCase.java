package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.SearchProduct;
import gov.justucuman.seed.domain.port.out.ProductSearchPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchProductUseCase implements SearchProduct {

    private final ProductSearchPort productSearchPort;

    @Override
    public List<Product> perform(String term, String value) {
        return productSearchPort.searchByParameter(term, value);
    }
}
