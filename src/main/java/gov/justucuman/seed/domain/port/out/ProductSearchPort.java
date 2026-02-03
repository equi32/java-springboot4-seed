package gov.justucuman.seed.domain.port.out;

import gov.justucuman.seed.domain.model.Product;

import java.util.List;

public interface ProductSearchPort {
    void indexProduct(Product product);
    void deleteProductFromIndex(String productId);
    List<Product> search(String query);
    List<Product> searchByParameter(String parameter, String value);
}
