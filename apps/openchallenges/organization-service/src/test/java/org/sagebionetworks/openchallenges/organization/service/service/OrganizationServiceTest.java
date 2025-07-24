package org.sagebionetworks.openchallenges.organization.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.mapper.OrganizationMapper;
import org.sagebionetworks.openchallenges.organization.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

  @Mock
  private OrganizationRepository organizationRepository;

  @Mock
  private ChallengeContributionRepository challengeContributionRepository;

  @Mock
  private OrganizationMapper organizationMapper;

  OrganizationService organizationService;

  @BeforeEach
  void setup() {
    organizationService = new OrganizationService(
      organizationRepository,
      challengeContributionRepository
    );
  }

  @Test
  void getOrganization_ShouldReturnOrganizationDto_WhenValidIdentifierPassed() {
    // Create valid identifier
    String identifier = "123";
    Long orgId = 123L;
    String orgLogin = String.valueOf(identifier);

    OrganizationEntity orgEntity = new OrganizationEntity();
    orgEntity.setId(orgId);

    OrganizationDto expectedDto = new OrganizationDto();
    expectedDto.setId(orgId);

    // Stubbing configuration to simulate a specific behavior of the organizationRepository mock
    // object to control its response and verify the behavior of the tested code that interacts with
    // it.
    when(organizationRepository.findByIdOrLogin(orgId, orgLogin)).thenReturn(
      Optional.of(orgEntity)
    );

    // get the organization using the identifier
    OrganizationDto response = organizationService.getOrganization(identifier);

    // Test that the organization is in the repo, is not null, that the id can be pulled,
    // findByIdOrLogin method is called, and no interactions with the organizationMapper (verifies
    // interactions w/Mock object)

    verify(organizationRepository).findByIdOrLogin(orgId, orgLogin);
    assertNotNull(response);
    assertEquals(expectedDto.getId(), response.getId());
    verifyNoInteractions(organizationMapper);
  }

  @Test
  void getOrganization_ShouldThrowOrganizationNotFoundException_WhenInvalidIdentifierPassed() {
    // Create invalid identifier
    String invalidIdentifier = "abc";

    // Stubbing configuration to simulate a specific behavior of the organizationRepository mock
    // object to control its response and verify the behavior of the tested code that interacts with
    // it.
    when(organizationRepository.findByIdOrLogin(any(), any())).thenReturn(Optional.empty());

    // Test that calling getOrganization throws an error
    assertThrows(OrganizationNotFoundException.class, () -> {
      organizationService.getOrganization(invalidIdentifier);
    });
    /* Test thatfindByIdOrLogin method is called, and no interactions with the organizationMapper
    (verifies interactions w/Mock object)*/

    verify(organizationRepository).findByIdOrLogin(any(), any());
    verifyNoInteractions(organizationMapper);
  }
}
