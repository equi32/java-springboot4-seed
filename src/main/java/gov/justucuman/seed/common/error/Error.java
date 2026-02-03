package gov.justucuman.seed.common.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Schema(
        name = "Error",
        description = "An error representation"
)
public record Error(
        URI type,
        String code,
        Integer status,
        String title,
        String message,
        String instance,
        List<ErrorDetail> details
) {
}
