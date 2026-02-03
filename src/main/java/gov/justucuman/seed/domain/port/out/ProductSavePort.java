package gov.justucuman.seed.domain.port.out;

import gov.justucuman.seed.domain.model.Product;

public interface ProductSavePort {
    Product perform(Product product);
}
