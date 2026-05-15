package gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.repository;

import gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.entity.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {}
