package gov.justucuman.seed.infrastructure.adapter.input.rest.mapper;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.CreateProductRequest;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import gov.justucuman.seed.infrastructure.adapter.output.persistence.jpa.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateProductMapper {
    CreateProductMapper INSTANCE = Mappers.getMapper(CreateProductMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "withId", ignore = true)
    })
    Product toDomain(CreateProductRequest request);
    ProductResponse toResponse(Product product);
    ProductEntity toEntity(Product product);
    @Mappings({
            @Mapping(target = "withId", ignore = true)
    })
    Product toDomain(ProductEntity product);
}
