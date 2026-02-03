package gov.justucuman.seed.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Slf4j
@Schema(
        name = "ErrorResponse",
        description = "An error response description"
)
public record ErrorResponse(Error error) implements Serializable {

    public ResponseEntity<ErrorResponse> toResponseEntity(Exception ex) {
        log.error("Exception captured", ex);
        return ResponseEntity.status(this.error.status()).contentType(MediaType.APPLICATION_JSON).body(this);
    }
}
