package com.reliaquest.api.exception;

import com.reliaquest.api.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String VALIDATION_ERROR_TITLE = "Validation Error";
  private static final String INVALID_REQUEST_CONTENT = "Invalid request content";
  private static final String INVALID_REQUEST_PARAMS = "Invalid request parameters";

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
    logger.warn("Employee service exception: {}", ex.getMessage());

    ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request,
            null
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    logger.warn("Constraint violation: {}", ex.getMessage());

    List<ErrorResponse.ValidationError> validationErrors = ex.getConstraintViolations()
            .stream()
            .map(violation -> ErrorResponse.ValidationError.builder()
                    .field(violation.getPropertyPath().toString())
                    .message(violation.getMessage())
                    .rejectedValue(violation.getInvalidValue() != null ?
                            violation.getInvalidValue().toString() : null)
                    .build())
            .collect(Collectors.toList());

    ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            VALIDATION_ERROR_TITLE,
            INVALID_REQUEST_PARAMS,
            request,
            validationErrors
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "An unexpected error occurred",
            request,
            null
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    logger.warn("Validation error occurred: {}", ex.getMessage());

    List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::mapToValidationError)
            .collect(Collectors.toList());

    ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            VALIDATION_ERROR_TITLE,
            INVALID_REQUEST_CONTENT,
            request,
            validationErrors
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  private ErrorResponse buildErrorResponse( HttpStatus status, String errorTitle,
          String message, WebRequest request, List<ErrorResponse.ValidationError> validationErrors) {

    return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(errorTitle)
            .message(message)
            .path(extractPath(request))
            .validationErrors(validationErrors)
            .build();
  }

  private ErrorResponse.ValidationError mapToValidationError(FieldError fieldError) {
      return ErrorResponse.ValidationError.builder()
              .field(fieldError.getField())
              .message(fieldError.getDefaultMessage())
              .rejectedValue(fieldError.getRejectedValue() != null ?
                      fieldError.getRejectedValue().toString() : null)
              .build();
  }

  private String extractPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}