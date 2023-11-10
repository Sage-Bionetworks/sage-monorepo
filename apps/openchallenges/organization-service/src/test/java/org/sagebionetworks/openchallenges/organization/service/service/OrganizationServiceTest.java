package org.sagebionetworks.openchallenges.organization.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
public class OrganizationServiceTest {
  @Mock private OrganizationRepository organizationRepository;
  private OrganizationService organizationService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    organizationService = new OrganizationService(organizationRepository);
  }

  @Test
  public void GetAndListOrganization_ShouldThrowErrors_WhenPassedMissingOrBadOrganizationKey() {
    // Mock the necessary objects
    Long id = 1L;
    String login = "dream";
    String altId = "75";

    OrganizationEntity organizationEntity = new OrganizationEntity();
    organizationEntity.setId(345L);
    organizationEntity.setName("test1");
    organizationEntity.setEmail("test1@gm.com");
    organizationEntity.setLogin("test1");
    organizationEntity.setAvatarKey("ava1");
    organizationEntity.setWebsiteUrl("website1");
    organizationEntity.setChallengeCount(2);
    organizationEntity.setChallengeContributions(null);
    organizationEntity.setDescription("desc1");
    organizationEntity.setCreatedAt(null);
    organizationEntity.setUpdatedAt(null);
    organizationEntity.setAcronym("aca1");

    // Call the findByIdOrLogin method
    Optional<OrganizationEntity> entityResult = organizationRepository.findByIdOrLogin(id, login);

    // Create query to pass to list organizations
    OrganizationSearchQueryDto query = new OrganizationSearchQueryDto();
    Page<OrganizationEntity> organizationEntityPage = mock(Page.class);
    List<OrganizationEntity> organizationEntities =
        Collections.singletonList(new OrganizationEntity());
    // Create mock results for the org entitites
    when(organizationEntityPage.getContent()).thenReturn(organizationEntities);
    when(organizationEntityPage.getNumber()).thenReturn(0);
    when(organizationEntityPage.getSize()).thenReturn(10);
    when(organizationEntityPage.getTotalElements()).thenReturn(1L);
    when(organizationEntityPage.getTotalPages()).thenReturn(1);
    when(organizationEntityPage.hasNext()).thenReturn(false);
    when(organizationEntityPage.hasPrevious()).thenReturn(false);

    // call methods and assert results match
    assertThrows(
        OrganizationNotFoundException.class, () -> organizationService.getOrganization(login));
    assertThrows(NullPointerException.class, () -> organizationService.listOrganizations(query));
    assertThrows(
        OrganizationNotFoundException.class, () -> organizationService.getOrganization(altId));
    // assertThat(result.getOrganizations().size()).isEqualTo(1);
    assertThat(organizationEntityPage.getNumber()).isEqualTo(0);
    assertThat(organizationEntityPage.getSize()).isEqualTo(10);
    assertThat(organizationEntityPage.getTotalElements()).isEqualTo(1L);
    assertThat(organizationEntityPage.getTotalPages()).isEqualTo(1);
    assertThat(organizationEntityPage.hasNext()).isEqualTo(false);
    assertThat(organizationEntityPage.hasPrevious()).isEqualTo(false);
  }
}
