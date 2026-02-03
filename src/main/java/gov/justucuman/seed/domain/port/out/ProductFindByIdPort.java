package gov.justucuman.seed.domain.port.out;

import gov.justucuman.seed.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductFindByIdPort {
    Optional<Product> perform(UUID id);
}
