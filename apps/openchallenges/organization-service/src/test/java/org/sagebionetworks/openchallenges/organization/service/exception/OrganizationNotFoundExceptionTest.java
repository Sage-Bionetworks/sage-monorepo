package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class OrganizationNotFoundExceptionTest {

  @Test
  public void OrganizationNotFoundException_ShouldReturnDetail_WhenDetailKeyIsPassed() {
    // Set up the input detail
    String detail = "Errordetail";

    // Create an instance of OrganizationNotFoundException
    OrganizationNotFoundException exception = new OrganizationNotFoundException(detail);

    // Verify the properties of the exception
    assertThat(exception.getDetail()).isEqualTo(detail);
  }
}
