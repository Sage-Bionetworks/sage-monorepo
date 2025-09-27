package org.sagebionetworks.openchallenges.challenge.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.configuration.AppProperties;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.OrganizationDto;
import org.sagebionetworks.openchallenges.challenge.service.service.TokenExchangeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Client for communicating with the OpenChallenges Organization Service using JWT tokens.
 *
 * This client uses OAuth 2.0 Token Exchange (On-Behalf-Of) to obtain properly scoped
 * JWT tokens for service-to-service communication on behalf of users.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrganizationServiceClient {

  private final RestClient restClient;
  private final TokenExchangeService tokenExchangeService;
  private final AppProperties appProperties;

  private static final String ORGANIZATION_SERVICE_AUDIENCE = "organization-service";

  /**
   * Gets an organization by ID.
   *
   * @param orgId the organization ID
   * @return the organization details
   * @throws RuntimeException if the organization is not found or service call fails
   */
  public OrganizationDto getOrganization(Long orgId) {
    String token = tokenExchangeService.exchangeTokenForService(
      ORGANIZATION_SERVICE_AUDIENCE,
      "read:organizations"
    );

    try {
      return restClient
        .get()
        .uri(appProperties.organizationService().baseUrl() + "/v1/organizations/{org}", orgId)
        .header("Authorization", "Bearer " + token)
        .retrieve()
        .body(OrganizationDto.class);
    } catch (RestClientException ex) {
      log.error("Failed to get organization {}: {}", orgId, ex.getMessage());
      throw new RuntimeException("Failed to retrieve organization", ex);
    }
  }

  /**
   * Creates a challenge participation for an organization.
   *
   * @param org the organization ID
   * @param request the participation creation request
   * @return the created challenge participation details
   * @throws RuntimeException if the creation fails
   */
  public ChallengeParticipationDto createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto request
  ) {
    String token = tokenExchangeService.exchangeTokenForService(
      ORGANIZATION_SERVICE_AUDIENCE,
      "update:organizations"
    );

    try {
      return restClient
        .post()
        .uri(
          appProperties.organizationService().baseUrl() + "/v1/organizations/{org}/participations",
          org
        )
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(ChallengeParticipationDto.class);
    } catch (RestClientException ex) {
      log.error(
        "Failed to create challenge participation for organization {}: {}",
        org,
        ex.getMessage()
      );
      throw new RuntimeException("Failed to create challenge participation", ex);
    }
  }

  /**
   * Deletes a challenge participation for an organization.
   *
   * @param org the organization ID
   * @param challengeId the challenge ID
   * @param role the role of the participation to delete
   * @throws RuntimeException if the deletion fails
   */
  public void deleteChallengeParticipation(String org, Long challengeId, String role) {
    String token = tokenExchangeService.exchangeTokenForService(
      ORGANIZATION_SERVICE_AUDIENCE,
      "update:organizations"
    );

    try {
      restClient
        .delete()
        .uri(
          appProperties.organizationService().baseUrl() +
          "/v1/organizations/{org}/participations/{challengeId}/role/{role}",
          org,
          challengeId,
          role
        )
        .header("Authorization", "Bearer " + token)
        .retrieve()
        .toBodilessEntity();

      log.debug(
        "Successfully deleted challenge participation for org: {}, challenge: {}, role: {}",
        org,
        challengeId,
        role
      );
    } catch (RestClientException ex) {
      log.error(
        "Failed to delete challenge participation for organization {} challenge {} role {}: {}",
        org,
        challengeId,
        role,
        ex.getMessage()
      );
      throw new RuntimeException("Failed to delete challenge participation", ex);
    }
  }
}
