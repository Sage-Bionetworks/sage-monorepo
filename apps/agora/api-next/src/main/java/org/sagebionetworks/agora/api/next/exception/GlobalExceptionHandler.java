package org.sagebionetworks.agora.api.next.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.explorers.UnhandledExceptionReporter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  static final String UNEXPECTED_ERROR_DETAIL = "An unexpected error occurred";

  @ExceptionHandler(ComparisonToolConfigNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleComparisonToolConfigNotFound(
    ComparisonToolConfigNotFoundException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.ENTITY_NOT_FOUND.getTitle())
      .status(ErrorConstants.ENTITY_NOT_FOUND.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    return ResponseEntity.status(ErrorConstants.ENTITY_NOT_FOUND.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(NominatedDrugNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleNominatedDrugNotFound(
    NominatedDrugNotFoundException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.ENTITY_NOT_FOUND.getTitle())
      .status(ErrorConstants.ENTITY_NOT_FOUND.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    return ResponseEntity.status(ErrorConstants.ENTITY_NOT_FOUND.getStatus())
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
    return ResponseEntity.status(ErrorConstants.INVALID_FILTER.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<BasicErrorDto> handleIllegalArgument(
    IllegalArgumentException ex,
    NativeWebRequest request,
    Locale locale
  ) {
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.BAD_REQUEST.getTitle())
      .status(ErrorConstants.BAD_REQUEST.getStatus().value())
      .detail(ex.getMessage())
      .instance(resolveInstance(request))
      .build();
    return ResponseEntity.status(ErrorConstants.BAD_REQUEST.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<BasicErrorDto> handleGenericException(
    Exception ex,
    NativeWebRequest request,
    Locale locale
  ) {
    log.error(
      "Unhandled exception for {} {}",
      resolveMethod(request),
      resolveInstance(request),
      ex
    );
    UnhandledExceptionReporter.report(ex);
    BasicErrorDto errorDto = BasicErrorDto.builder()
      .title(ErrorConstants.INTERNAL_SERVER_ERROR.getTitle())
      .status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus().value())
      .detail(UNEXPECTED_ERROR_DETAIL)
      .instance(resolveInstance(request))
      .build();
    return ResponseEntity.status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus())
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(errorDto);
  }

  private String resolveMethod(NativeWebRequest request) {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    return servletRequest != null ? servletRequest.getMethod() : "";
  }

  private String resolveInstance(NativeWebRequest request) {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    return servletRequest != null ? servletRequest.getRequestURI() : "";
  }
}
