package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class ErrorConstantsTest {

  @Test
  public void ErrorConstants_ShouldReturnErrors_WhenErrorConstantsKeyIsPassed() {

    // Get the ImageHeightNotSpecified error constant
    ErrorConstants constant = ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED;

    // Verify the properties of the constant
    assertThat(constant.getType()).isEqualTo("IMAGE-SERVICE-1000");
    assertThat(constant.getTitle()).isEqualTo("Image height not found");
    assertThat(constant.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
