package org.sagebionetworks.openchallenges.challenge.service.client;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.OrganizationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Feign client for communicating with the OpenChallenges Organization Service
 */
@FeignClient(
  name = "organization-service",
  url = "${openchallenges-challenge-service.organization-service.base-url:http://openchallenges-organization-service:8084}",
  path = "/v1"
)
public interface OrganizationServiceClient {
  /**
   * Gets an organization by ID
   *
   * @param orgId the organization ID
   * @return the organization details
   */
  @GetMapping("/organizations/{orgId}")
  OrganizationDto getOrganization(@PathVariable("orgId") Long orgId);

  /**
   * Creates a challenge participation for an organization
   *
   * @param org the organization ID
   * @param request the participation creation request
   * @return the created challenge participation details
   */
  @PostMapping("/organizations/{org}/participations")
  ChallengeParticipationDto createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto request
  );

  /**
   * Deletes a challenge participation for an organization
   *
   * @param org the organization ID
   * @param challengeId the challenge ID
   * @param role the role of the participation to delete
   * @return void
   */
  @DeleteMapping("/organizations/{org}/participations/challenge/{challengeId}/role/{role}")
  Void deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  );
}
