package org.sagebionetworks.openchallenges.organization.service.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;

@ExtendWith(MockitoExtension.class)
class OrganizationApiSecurityTest {

  @Mock
  private OrganizationService organizationService;

  private OrganizationApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    delegate = new OrganizationApiDelegateImpl(organizationService);
  }

  @Test
  void shouldCreateDelegateInstance() {
    // Simple test to verify the delegate can be created
    assertNotNull(delegate);
  }
}
