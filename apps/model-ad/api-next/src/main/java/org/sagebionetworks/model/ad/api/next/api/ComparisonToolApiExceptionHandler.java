package org.sagebionetworks.model.ad.api.next.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(
  assignableTypes = { ModelOverviewApiController.class, DiseaseCorrelationApiController.class }
)
public class ComparisonToolApiExceptionHandler {

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
    MethodArgumentTypeMismatchException ex,
    NativeWebRequest request
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
    String instance = resolveInstance(request);
    Map<String, Object> body = ComparisonToolApiHelper.buildProblemJson(
      HttpStatus.BAD_REQUEST,
      HttpStatus.BAD_REQUEST.getReasonPhrase(),
      detail,
      instance
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(body);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(
    ResponseStatusException ex,
    NativeWebRequest request
  ) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    String instance = resolveInstance(request);
    String detail = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
    Map<String, Object> body = ComparisonToolApiHelper.buildProblemJson(
      status,
      status.getReasonPhrase(),
      detail,
      instance
    );
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(body);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(
    MissingServletRequestParameterException ex,
    NativeWebRequest request
  ) {
    String detail;
    if ("category".equals(ex.getParameterName())) {
      detail = ComparisonToolApiHelper.CATEGORY_REQUIREMENT_MESSAGE;
    } else {
      detail = String.format("Query parameter %s is required", ex.getParameterName());
    }
    String instance = resolveInstance(request);
    Map<String, Object> body = ComparisonToolApiHelper.buildProblemJson(
      HttpStatus.BAD_REQUEST,
      HttpStatus.BAD_REQUEST.getReasonPhrase(),
      detail,
      instance
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.APPLICATION_PROBLEM_JSON)
      .body(body);
  }

  private String resolveInstance(NativeWebRequest request) {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    return servletRequest != null ? servletRequest.getRequestURI() : "";
  }
}
