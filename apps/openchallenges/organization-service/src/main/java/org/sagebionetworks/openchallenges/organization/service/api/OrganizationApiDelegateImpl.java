package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  private final OrganizationService organizationService;

  public OrganizationApiDelegateImpl(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @Override
  public ResponseEntity<Void> deleteOrganization(String identifier) {
    organizationService.deleteOrganization(identifier);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<OrganizationsPageDto> listOrganizations(OrganizationSearchQueryDto query) {
    return ResponseEntity.ok(organizationService.listOrganizations(query));
  }

  @Override
  public ResponseEntity<OrganizationDto> getOrganization(String identifier) {
    return ResponseEntity.ok(organizationService.getOrganization(identifier));
  }
}
