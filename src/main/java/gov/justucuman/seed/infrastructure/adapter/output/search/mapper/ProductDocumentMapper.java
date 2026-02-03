package gov.justucuman.seed.infrastructure.adapter.output.search.mapper;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.model.ProductStatus;
import gov.justucuman.seed.infrastructure.adapter.output.search.document.ProductDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductDocumentMapper {
    ProductDocumentMapper INSTANCE = Mappers.getMapper(ProductDocumentMapper.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    ProductDocument toDocument(Product product);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "withId", ignore = true)
    Product toDomain(ProductDocument document);

    @Named("stringToStatus")
    default ProductStatus stringToStatus(String status) {
        return status != null ? ProductStatus.valueOf(status) : null;
    }

    @Named("statusToString")
    default String statusToString(ProductStatus status) {
        return status != null ? status.name() : null;
    }
}
