package gov.justucuman.seed.application;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.in.GetAllExternalProduct;
import gov.justucuman.seed.domain.port.out.ExternalProductPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllExternalProductUseCase implements GetAllExternalProduct {

    private final ExternalProductPort externalProductPort;

    @Override
    public List<Product> perform() {
        return externalProductPort.getAll();
    }
}
