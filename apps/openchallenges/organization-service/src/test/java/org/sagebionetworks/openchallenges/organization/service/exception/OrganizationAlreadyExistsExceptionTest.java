package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class OrganizationAlreadyExistsExceptionTest {

  @Test
  public void OrganizationAlreadyExistsException_ShouldReturnMessage_WhenMessageKeyIsPassed() {
    // Set up the input detail
    String message = "Errordetail";

    // Create an instance of OrganizationAlreadyExistsException
    OrganizationAlreadyExistsException exception = new OrganizationAlreadyExistsException(message);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(message);
  }
}
