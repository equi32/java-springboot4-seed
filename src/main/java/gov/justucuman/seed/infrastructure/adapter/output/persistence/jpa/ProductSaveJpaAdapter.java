package gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductSavePort;
import gov.justucuman.seed.infrastructure.adapter.input.rest.mapper.CreateProductMapper;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.entity.ProductEntity;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSaveJpaAdapter implements ProductSavePort {

    private final JpaProductRepository repository;

    public Product perform(Product product) {
        log.info("Starting productSaveAdapter: {}", product);
        ProductEntity productEntity = CreateProductMapper.INSTANCE.toEntity(product);
        log.info("Entity generated {}", productEntity);
        ProductEntity savedEntity = repository.save(productEntity);
        return CreateProductMapper.INSTANCE.toDomain(savedEntity);
    }
}
