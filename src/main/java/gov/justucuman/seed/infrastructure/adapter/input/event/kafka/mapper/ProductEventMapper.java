package gov.justucuman.seed.infrastructure.adapter.input.event.kafka.mapper;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.infrastructure.adapter.output.event.dto.ProductEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductEventMapper {
    ProductEventMapper INSTANCE = Mappers.getMapper(ProductEventMapper.class);

    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomain(ProductEvent event);

    @Named("stringToStatus")
    default ProductStatus stringToStatus(String status) {
        return status != null ? ProductStatus.valueOf(status) : null;
    }
}
