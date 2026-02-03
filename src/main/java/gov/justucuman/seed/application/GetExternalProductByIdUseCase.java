package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.GetExternalProductById;
import gov.justucuman.seed.domain.port.out.ExternalProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetExternalProductByIdUseCase implements GetExternalProductById {

    private final ExternalProductPort externalProductPort;

    @Override
    public Product perform(Integer id) {
        return externalProductPort.getById(id);
    }
}
