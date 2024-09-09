package org.sagebionetworks.openchallenges.image.service.exception;

import org.sagebionetworks.openchallenges.image.service.model.dto.BasicErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleBindException(
    BindException ex,
    HttpHeaders headers,
    HttpStatus status,
    WebRequest request
  ) {
    BindingResult result = ex.getBindingResult();
    FieldError fieldError = result.getFieldErrors().get(0);

    BadRequestException exception = new BadRequestException(
      String.format(
        "Invalid value '%s' for for property '%s'",
        fieldError.getRejectedValue(),
        fieldError.getField()
      )
    );

    BasicErrorDto error = BasicErrorDto.builder()
      .title(exception.getTitle())
      .status(exception.getStatus().value())
      .detail(exception.getDetail())
      .type(exception.getType())
      .build();

    return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
  }
}
