package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class OrganizationAlreadyExistsExceptionTest {

  @Test
  public void OrganizationAlreadyExistsException_ShouldReturnDetail_WhenDetailKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of OrganizationAlreadyExistsException
    OrganizationAlreadyExistsException exception = new OrganizationAlreadyExistsException(detail);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(detail);
  }
}
