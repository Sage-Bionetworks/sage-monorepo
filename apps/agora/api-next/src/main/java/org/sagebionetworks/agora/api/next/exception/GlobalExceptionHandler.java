package org.sagebionetworks.agora.api.next.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
