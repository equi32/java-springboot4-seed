package gov.justucuman.seed.domain.port.in;

import java.util.UUID;

public interface DeleteProductById {
    void perform(UUID id);
}
