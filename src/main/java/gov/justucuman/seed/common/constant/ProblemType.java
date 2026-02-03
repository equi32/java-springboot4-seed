package gov.justucuman.seed.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Getter
@AllArgsConstructor
public enum ProblemType {
    HTTP_BAD_REQUEST(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400"),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.name(),
            HttpStatus.BAD_REQUEST),
    HTTP_METHOD_NOT_FOUND(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/404"),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            HttpStatus.NOT_FOUND.name(),
            HttpStatus.NOT_FOUND),
    HTTP_METHOD_NOT_SUPPORTED(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/405"),
            HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
            HttpStatus.METHOD_NOT_ALLOWED.name(),
            HttpStatus.METHOD_NOT_ALLOWED),
    HTTP_MESSAGE_NOT_READABLE(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400"),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.name(),
            HttpStatus.BAD_REQUEST),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/415"),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.name(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    ARGUMENT_NOT_VALID(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400"),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.name(),
            HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(
            URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/500"),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            HttpStatus.INTERNAL_SERVER_ERROR);

    private final URI type;
    private final String title;
    private final String code;
    private final HttpStatus status;
}
