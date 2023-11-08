package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BadRequestExceptionTest {

  @Test
  public void BadRequestException_ShouldReturnType_WhenTypeKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of BadRequestException
    BadRequestException exception = new BadRequestException(detail);

    // Verify the properties of the exception
    assertThat(exception.getType()).isEqualTo(ErrorConstants.BAD_REQUEST.getType());
  }

  @Test
  public void BadRequestException_ShouldReturnStatus_WhenStatusKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of BadRequestException
    BadRequestException exception = new BadRequestException(detail);

    // Verify the properties of the exception
    assertThat(exception.getStatus()).isEqualTo(ErrorConstants.BAD_REQUEST.getStatus());
  }

  @Test
  public void BadRequestException_ShouldReturnDetail_WhenDetailKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of BadRequestException
    BadRequestException exception = new BadRequestException(detail);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(detail);
  }
}
