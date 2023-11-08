package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@SpringJUnitConfig
@WebMvcTest(ControllerAdvisor.class)
public class ControllerAdvisorTest {

  @Autowired private MockMvc mockMvc;

  @Mock private BindException bindException;

  @InjectMocks private ControllerAdvisor controllerAdvisor;

  @Test
  public void bindException_ShouldReturnResultAndException_WhenMockErrorIsPassed() {
    MockitoAnnotations.openMocks(this);

    // String bindException = "There is an error";
    String header = "Exception header";
    String status = HttpStatus.BAD_REQUEST;
    String webRequest = "Exception web request";

    // Create a mock FieldError
    FieldError fieldError =
        new FieldError(
            "objectName",
            "fieldName",
            "rejectedValue",
            false,
            new String[] {"errorCode"},
            null,
            "defaultMessage");

    // Create a mock BindingResult
    BindingResult bindingResult = new BindException(new Object(), "objectName");
    bindingResult.addError(fieldError);
    BindingResult handleBindException =
        new HandleBindException(bindException, header, status, webRequest);

    // // Set up the mock BindException to return the mock BindingResult
    when(bindException.getBindingResult()).thenReturn(bindingResult);
    when(bindException.handleBindException()).thenReturn(handleBindException);

    assertThat(bindException.getBindingResult()).isEqualTo(bindingResult);
    assertThat(bindException.handleBindException()).isEqualTo(handleBindException.get(0));
  }
}
