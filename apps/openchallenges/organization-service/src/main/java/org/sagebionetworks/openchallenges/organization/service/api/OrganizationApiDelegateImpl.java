package org.sagebionetworks.openchallenges.organization.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationUpdateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  private final OrganizationService organizationService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_create:organizations')")
  public ResponseEntity<OrganizationDto> createOrganization(
    OrganizationCreateRequestDto organizationCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes (from JWT)
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info("User {} is creating an organization", authentication.getName());
    }

    OrganizationDto createdOrganization = organizationService.createOrganization(
      organizationCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_delete:organizations')")
  public ResponseEntity<Void> deleteOrganization(String organizationId) {
    // Log the authenticated user for audit purposes
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info("User {} is deleting organization: {}", authentication.getName(), organizationId);
    }

    organizationService.deleteOrganization(organizationId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_update:organizations')")
  public ResponseEntity<OrganizationDto> updateOrganization(
    String org,
    OrganizationUpdateRequestDto organizationUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info("User {} is updating organization: {}", authentication.getName(), org);
    }

    OrganizationDto updatedOrganization = organizationService.updateOrganization(
      org,
      organizationUpdateRequestDto
    );
    return ResponseEntity.ok(updatedOrganization);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:organizations')")
  public ResponseEntity<OrganizationsPageDto> listOrganizations(OrganizationSearchQueryDto query) {
    return ResponseEntity.ok(organizationService.listOrganizations(query));
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:organizations')")
  public ResponseEntity<OrganizationDto> getOrganization(String identifier) {
    return ResponseEntity.ok(organizationService.getOrganization(identifier));
  }
}
