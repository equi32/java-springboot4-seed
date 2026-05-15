package gov.justucuman.seed.infrastructure.adapter.input.rest.mapper;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.infrastructure.adapter.input.rest.dto.ProductResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GetProductMapper {
    GetProductMapper INSTANCE = Mappers.getMapper(GetProductMapper.class);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponse(List<Product> product);
}
