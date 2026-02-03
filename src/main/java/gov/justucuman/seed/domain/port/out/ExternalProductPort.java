package gov.justucuman.seed.domain.port.out;

import gov.justucuman.seed.domain.model.Product;

import java.util.List;

public interface ExternalProductPort {
    Product getById(Integer id);
    List<Product> getAll();
}
