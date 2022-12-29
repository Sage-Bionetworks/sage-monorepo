package org.sagebionetworks.challenge.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalControllerAdvice {

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<String> handleUnauthorized(HttpClientErrorException e) {
    return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
  }
}
