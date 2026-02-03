package gov.justucuman.seed.domain.port.out;

import gov.justucuman.seed.domain.model.Product;

import java.util.List;

public interface ProductFindAllPort {
    List<Product> perform();
}
