package gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.repository;

import gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
}
