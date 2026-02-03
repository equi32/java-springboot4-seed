package gov.justucuman.seed.domain.port.in;

import gov.justucuman.seed.domain.model.Product;

public interface CreateProduct {
    Product perform(Product product);
}
