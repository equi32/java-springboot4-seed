package gov.justucuman.seed.domain.port.out;

import java.util.UUID;

public interface ProductDeleteByIdPort {
    void perform(UUID id);
}
