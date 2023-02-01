package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  @Autowired OrganizationService organizationService;

  @Override
  public ResponseEntity<OrganizationsPageDto> listOrganizations(
      Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(organizationService.listOrganizations(pageNumber, pageSize));
  }

  @Override
  public ResponseEntity<OrganizationDto> getOrganization(String organizationLogin) {
    return ResponseEntity.ok(organizationService.getOrganization(organizationLogin));
  }
}
