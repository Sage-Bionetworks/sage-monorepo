package org.sagebionetworks.challenge.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "City not found");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);

    // return handleExceptionInternal(ex, null, headers, status, request);
  }
  // public ResponseEntity<Object> handleBindException(
  //   BindException ex, HttpHeaders headers,
  //   HttpStatus status, WebRequest request) {

  //     // BadRequestException("my message");

  //     Map<String, Object> body = new LinkedHashMap<>();
  //     body.put("message", "City not found");

  //     return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  // }
}
