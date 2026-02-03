package gov.justucuman.seed.domain.model;

import java.util.Arrays;

public enum ProductStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    DISCONTINUED,
    PRE_ORDER;

    public static ProductStatus fromName(String name) {
        return Arrays.stream(ProductStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
