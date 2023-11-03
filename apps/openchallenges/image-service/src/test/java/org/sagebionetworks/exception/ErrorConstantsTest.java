package org.sagebionetworks.openchallenges.image.service.exception;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.image.service.exception.ErrorConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ErrorConstantsTest {

  @Test
  public void ErrorConstants_ShouldReturnErrorsWhenHeightNotGiven() {

    // Get the ImageHeightNotSpecified error constant
    ErrorConstants constant = ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED;

    // Verify the properties of the constant
    assertThat(constant.getType()).isEqualTo("IMAGE-SERVICE-1000");
    assertThat(constant.getTitle()).isEqualTo("Image height not found");
    assertThat(constant.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    
  }

}