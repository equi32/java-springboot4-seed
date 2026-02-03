package gov.justucuman.seed.infrastructure.adapter.output.external.mapper;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.infrastructure.adapter.output.external.dto.ExternalProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class, ProductStatus.class}
)
public interface ExternalProductMapper {
    ExternalProductMapper INSTANCE = Mappers.getMapper(ExternalProductMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "stock", constant = "1")
    @Mapping(target = "status", expression = "java(ProductStatus.OUT_OF_STOCK)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomain(ExternalProductResponse response);
}
