package org.sagebionetworks.openchallenges.challenge.service.client;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.OrganizationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
