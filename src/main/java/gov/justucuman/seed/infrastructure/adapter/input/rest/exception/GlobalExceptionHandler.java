package gov.justucuman.seed.infrastructure.adapter.input.rest.exception;

import gov.justucuman.seed.application.exception.ProductNotFoundException;
import gov.justucuman.seed.common.constant.ProblemType;
import gov.justucuman.seed.common.error.Error;
import gov.justucuman.seed.common.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handle(
            NoHandlerFoundException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.HTTP_METHOD_NOT_FOUND.getType(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getCode(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getStatus().value(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorResponse> handle(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.HTTP_METHOD_NOT_SUPPORTED.getType(),
                        ProblemType.HTTP_METHOD_NOT_SUPPORTED.getCode(),
                        ProblemType.HTTP_METHOD_NOT_SUPPORTED.getStatus().value(),
                        ProblemType.HTTP_METHOD_NOT_SUPPORTED.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handle(
            Exception ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.INTERNAL_SERVER_ERROR.getType(),
                        ProblemType.INTERNAL_SERVER_ERROR.getCode(),
                        ProblemType.INTERNAL_SERVER_ERROR.getStatus().value(),
                        ProblemType.INTERNAL_SERVER_ERROR.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handle(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.HTTP_MESSAGE_NOT_READABLE.getType(),
                        ProblemType.HTTP_MESSAGE_NOT_READABLE.getCode(),
                        ProblemType.HTTP_MESSAGE_NOT_READABLE.getStatus().value(),
                        ProblemType.HTTP_MESSAGE_NOT_READABLE.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ErrorResponse> handle(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getType(),
                        ProblemType.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getCode(),
                        ProblemType.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getStatus().value(),
                        ProblemType.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handle(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.ARGUMENT_NOT_VALID.getType(),
                        ProblemType.ARGUMENT_NOT_VALID.getCode(),
                        ProblemType.ARGUMENT_NOT_VALID.getStatus().value(),
                        ProblemType.ARGUMENT_NOT_VALID.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handle(
            ProductNotFoundException ex, HttpServletRequest request) {
        return new ErrorResponse(
                new Error(
                        ProblemType.HTTP_METHOD_NOT_FOUND.getType(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getCode(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getStatus().value(),
                        ProblemType.HTTP_METHOD_NOT_FOUND.getTitle(),
                        ex.getMessage(),
                        request.getMethod().concat(" ").concat(request.getRequestURI()),
                        List.of()))
                .toResponseEntity(ex);
    }
}
