package org.sagebionetworks.model.ad.api.next.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ModelNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleModelNotFound(
    ModelNotFoundException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.ENTITY_NOT_FOUND.getTitle())
      .status(ErrorConstants.ENTITY_NOT_FOUND.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.ENTITY_NOT_FOUND.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(InvalidObjectIdException.class)
  protected ResponseEntity<BasicErrorDto> handleInvalidObjectId(
    InvalidObjectIdException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.INVALID_OBJECT_ID.getTitle())
      .status(ErrorConstants.INVALID_OBJECT_ID.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.INVALID_OBJECT_ID.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(InvalidCategoryException.class)
  protected ResponseEntity<BasicErrorDto> handleInvalidCategory(
    InvalidCategoryException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.INVALID_CATEGORY.getTitle())
      .status(ErrorConstants.INVALID_CATEGORY.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.INVALID_CATEGORY.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(InvalidFilterException.class)
  protected ResponseEntity<BasicErrorDto> handleInvalidFilter(
    InvalidFilterException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.INVALID_FILTER.getTitle())
      .status(ErrorConstants.INVALID_FILTER.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.INVALID_FILTER.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(DataIntegrityException.class)
  protected ResponseEntity<BasicErrorDto> handleDataIntegrity(
    DataIntegrityException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.DATA_INTEGRITY_ERROR.getTitle())
      .status(ErrorConstants.DATA_INTEGRITY_ERROR.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    log.error("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.DATA_INTEGRITY_ERROR.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<BasicErrorDto> handleMethodArgumentTypeMismatch(
    MethodArgumentTypeMismatchException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    String detail;
    if ("itemFilterType".equals(ex.getName())) {
      detail = "Query parameter itemFilterType must be either 'include' or 'exclude' if provided";
    } else {
      detail = String.format(
        "Query parameter %s has invalid value '%s'",
        ex.getName(),
        ex.getValue()
      );
    }
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.BAD_REQUEST.getTitle())
      .status(ErrorConstants.BAD_REQUEST.getStatus().value())
      .detail(detail)
      .instance(resolveInstance(request))
      .build();
    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.BAD_REQUEST.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  /**
   * Override ResponseEntityExceptionHandler's method for handling
   * MissingServletRequestParameterException.
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
    MissingServletRequestParameterException ex,
    HttpHeaders headers,
    HttpStatusCode status,
    WebRequest request
  ) {
    String detail;
    if ("category".equals(ex.getParameterName())) {
      detail = ErrorConstants.CATEGORY_REQUIREMENT_MESSAGE;
    } else {
      detail = String.format("Query parameter %s is required", ex.getParameterName());
    }

    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.BAD_REQUEST.getTitle())
      .status(ErrorConstants.BAD_REQUEST.getStatus().value())
      .detail(detail)
      .instance(resolveInstance((NativeWebRequest) request))
      .build();

    log.warn("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return new ResponseEntity<>(errorDto, responseHeaders, ErrorConstants.BAD_REQUEST.getStatus());
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<BasicErrorDto> handleGenericException(
    Exception ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.INTERNAL_SERVER_ERROR.getTitle())
      .status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus().value())
      .detail("An unexpected error occurred")
      .instance(resolveInstance(request))
      .build();
    log.error("{} {}: uri={}, error={}",
      errorDto.getStatus(), errorDto.getTitle(), errorDto.getInstance(), errorDto.getDetail());
    return ResponseEntity.status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  private String resolveInstance(NativeWebRequest request) {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    return servletRequest != null ? servletRequest.getRequestURI() : "";
  }
}
