package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.openchallenges.image.service.exception.ControllerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringJUnitConfig
@WebMvcTest(ControllerAdvisor.class)
public class ControllerAdvisorTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BindException bindException;

    @InjectMocks
    private ControllerAdvisor controllerAdvisor;

    @Test
    public void testBindExceptionBindingResult() {
        MockitoAnnotations.openMocks(this);

        // Create a mock FieldError
        FieldError fieldError = new FieldError(
                "objectName", "fieldName", "rejectedValue", false, new String[]{"errorCode"}, null,
                "defaultMessage");

        // Create a mock BindingResult
        BindingResult bindingResult = new BindException(new Object(), "objectName");
        bindingResult.addError(fieldError);

        // // Set up the mock BindException to return the mock BindingResult
        when(bindException.getBindingResult()).thenReturn(bindingResult);

        assertThat(bindException.getBindingResult()).isEqualTo(bindingResult);

    }
}