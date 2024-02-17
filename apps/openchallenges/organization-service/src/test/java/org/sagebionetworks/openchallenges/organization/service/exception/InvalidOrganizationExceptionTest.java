package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InvalidOrganizationExceptionTest {

  @Test
  public void InvalidOrganizationException_ShouldReturnExceptionObject_WhenDetailKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of InvalidOrganizationException
    InvalidOrganizationException exception = new InvalidOrganizationException(detail);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(detail);
    assertThat(exception.getType()).isEqualTo(ErrorConstants.ENTITY_NOT_FOUND.getType());
    assertThat(exception.getStatus()).isEqualTo(ErrorConstants.ENTITY_NOT_FOUND.getStatus());
    assertThat(exception.getTitle()).isEqualTo(ErrorConstants.ENTITY_NOT_FOUND.getTitle());
  }
}
