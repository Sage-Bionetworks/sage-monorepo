package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationUpdateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(OrganizationApiDelegateImpl.class);

  private final OrganizationService organizationService;

  public OrganizationApiDelegateImpl(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<OrganizationDto> createOrganization(
    OrganizationCreateRequestDto organizationCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is creating an organization",
      user.getUsername(),
      user.getRole()
    );

    OrganizationDto createdOrganization = organizationService.createOrganization(
      organizationCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<Void> deleteOrganization(String identifier) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting organization: {}",
      user.getUsername(),
      user.getRole(),
      identifier
    );

    organizationService.deleteOrganization(identifier);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<OrganizationDto> updateOrganization(
    String org,
    OrganizationUpdateRequestDto organizationUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is updating organization: {}",
      user.getUsername(),
      user.getRole(),
      org
    );

    OrganizationDto updatedOrganization = organizationService.updateOrganization(
      org,
      organizationUpdateRequestDto
    );
    return ResponseEntity.ok(updatedOrganization);
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
