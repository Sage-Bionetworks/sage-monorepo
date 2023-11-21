package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class BadRequestExceptionTest {

  @Test
  public void BadRequestException_ShouldReturnExceptionConstructors_WhenKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of BadRequestException
    BadRequestException exception = new BadRequestException(detail);

    // Verify the properties of the exception
    assertThat(exception.getStatus()).isEqualTo(ErrorConstants.BAD_REQUEST.getStatus());
    assertThat(exception.getDetail()).isEqualTo(detail);
    assertThat(exception.getType()).isEqualTo(ErrorConstants.BAD_REQUEST.getType());
    assertThat(exception.getTitle()).isEqualTo(ErrorConstants.BAD_REQUEST.getTitle());
  }
}
