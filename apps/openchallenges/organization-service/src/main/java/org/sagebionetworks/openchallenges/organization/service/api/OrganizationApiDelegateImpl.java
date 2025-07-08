package org.sagebionetworks.openchallenges.organization.service.api;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  private final OrganizationService organizationService;

  public OrganizationApiDelegateImpl(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @Override
  @PreAuthorize("hasAuthority('organizations:delete')")
  public ResponseEntity<Void> deleteOrganization(String identifier) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    log.info(
      "User {} (role: {}) is deleting organization: {}",
      user.getUsername(),
      user.getRole(),
      identifier
    );

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
