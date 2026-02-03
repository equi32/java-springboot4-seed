package gov.justucuman.seed.domain.port.in;

import gov.justucuman.seed.domain.model.Product;

import java.util.List;

public interface SearchProduct {
    List<Product> perform(String term, String value);
}
