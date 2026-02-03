package gov.justucuman.seed.domain.port.in;

import gov.justucuman.seed.domain.model.Product;

public interface GetExternalProductById {
    Product perform(Integer id);
}
