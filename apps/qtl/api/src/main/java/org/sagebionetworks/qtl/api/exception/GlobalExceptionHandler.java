package org.sagebionetworks.qtl.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.sagebionetworks.qtl.api.model.dto.BasicErrorDto;
import org.springframework.http.HttpHeaders;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
    return ResponseEntity.status(ErrorConstants.DATA_INTEGRITY_ERROR.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<BasicErrorDto> handleIllegalArgument(
    IllegalArgumentException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    return ResponseEntity.status(ErrorConstants.BAD_REQUEST.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(
        BasicErrorDto.builder()
          .title(ErrorConstants.BAD_REQUEST.getTitle())
          .status(ErrorConstants.BAD_REQUEST.getStatus().value())
          .detail(ex.getMessage())
          .instance(resolveInstance(request))
          .build()
      );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<BasicErrorDto> handleMethodArgumentTypeMismatch(
    MethodArgumentTypeMismatchException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    String detail = String.format(
      "Query parameter %s has invalid value '%s'",
      ex.getName(),
      ex.getValue()
    );
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.BAD_REQUEST.getTitle())
      .status(ErrorConstants.BAD_REQUEST.getStatus().value())
      .detail(detail)
      .instance(resolveInstance(request))
      .build();
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
    String detail = String.format("Query parameter %s is required", ex.getParameterName());

    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.BAD_REQUEST.getTitle())
      .status(ErrorConstants.BAD_REQUEST.getStatus().value())
      .detail(detail)
      .instance(resolveInstance((NativeWebRequest) request))
      .build();

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
    return ResponseEntity.status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  private String resolveInstance(NativeWebRequest request) {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    return servletRequest != null ? servletRequest.getRequestURI() : "";
  }
}
