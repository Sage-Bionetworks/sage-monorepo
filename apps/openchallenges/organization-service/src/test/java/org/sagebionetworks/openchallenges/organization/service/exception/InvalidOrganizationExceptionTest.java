package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InvalidOrganizationExceptionTest {

  @Test
  public void InvalidOrganizationException_ShouldReturnExceptionConstructors_WhenKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of InvalidOrganizationException
    InvalidOrganizationException exception = new InvalidOrganizationException(detail);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(detail);
    assertThat(exception.getType()).isEqualTo(ErrorConstants.BAD_REQUEST.getType());
    assertThat(exception.getStatus()).isEqualTo(ErrorConstants.BAD_REQUEST.getStatus());
  }
}
