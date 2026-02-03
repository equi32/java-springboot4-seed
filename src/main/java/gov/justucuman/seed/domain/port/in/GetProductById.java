package gov.justucuman.seed.domain.port.in;

import gov.justucuman.seed.domain.model.Product;

import java.util.UUID;

public interface GetProductById {
    Product perform(UUID id);
}
