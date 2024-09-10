package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.image.service.model.dto.BasicErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

public class ControllerAdvisorTest {

  @Test
  public void HandleBindException_ShouldReturnStatusAndBody_WhenBothArePassed() {
    // Create a sample BindException
    BindException bindException = mock(BindException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError(
      "objectName",
      "fieldName",
      "rejectedValue",
      false,
      null,
      null,
      "error message"
    );
    List<FieldError> fieldErrors = new ArrayList<>();
    fieldErrors.add(fieldError);

    // Mock the behavior of the BindException and BindingResult
    when(bindException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    // Create the ControllerAdvisor instance
    ControllerAdvisor controllerAdvisor = new ControllerAdvisor();

    // Call handleBindException
    ResponseEntity<Object> responseEntity = controllerAdvisor.handleBindException(
      bindException,
      new HttpHeaders(),
      HttpStatus.BAD_REQUEST,
      mock(WebRequest.class)
    );

    // Verify the response
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    BasicErrorDto errorDto = (BasicErrorDto) responseEntity.getBody();
  }
}
