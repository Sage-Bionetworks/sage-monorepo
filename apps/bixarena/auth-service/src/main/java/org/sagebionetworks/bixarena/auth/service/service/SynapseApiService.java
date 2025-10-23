package org.sagebionetworks.bixarena.auth.service.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service for interacting with the Synapse REST API.
 */
@Service
@Slf4j
public class SynapseApiService {

  private static final String SYNAPSE_API_BASE_URL = "https://repo-prod.prod.sagebase.org";

  private final RestClient restClient;

  public SynapseApiService() {
    this.restClient = RestClient.builder().baseUrl(SYNAPSE_API_BASE_URL).build();
  }

  /**
   * Fetches the Synapse user profile of the caller (authenticated user).
   * This endpoint requires the request to be authenticated with a valid access token.
   *
   * @param accessToken The Synapse OAuth2 access token
   * @return The user profile, or null if the request fails
   */
  public SynapseUserProfile getUserProfile(String accessToken) {
    if (accessToken == null || accessToken.isBlank()) {
      log.warn("Cannot fetch Synapse user profile: access token is null or empty");
      return null;
    }

    try {
      log.debug("Fetching Synapse user profile for authenticated user");

      SynapseUserProfile profile = restClient
        .get()
        .uri("/repo/v1/userProfile")
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          log.warn(
            "Client error fetching Synapse user profile: status={}, body={}",
            response.getStatusCode(),
            new String(response.getBody().readAllBytes())
          );
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          log.error(
            "Server error fetching Synapse user profile: status={}, body={}",
            response.getStatusCode(),
            new String(response.getBody().readAllBytes())
          );
        })
        .body(SynapseUserProfile.class);

      if (profile != null) {
        log.info(
          "Successfully fetched Synapse user profile: ownerId={}, userName={}, firstName={}, lastName={}, email={}",
          profile.getOwnerId(),
          profile.getUserName(),
          profile.getFirstName(),
          profile.getLastName(),
          profile.getEmail()
        );
        log.debug("Full Synapse profile object: {}", profile);
      } else {
        log.warn("Received null profile from Synapse API");
      }

      return profile;
    } catch (Exception e) {
      log.error("Exception while fetching Synapse user profile: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * Represents a Synapse user profile.
   * Based on: https://rest-docs.synapse.org/rest/org/sagebionetworks/repo/model/UserProfile.html
   */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SynapseUserProfile {

    private String ownerId;
    private String etag;
    private String firstName;
    private String lastName;
    private String email;
    private String[] emails;
    private String[] openIds;
    private String userName;
    private String displayName;
    private String rStudioUrl;
    private String summary;
    private String position;
    private String location;
    private String industry;
    private String company;
    private String profilePicureFileHandleId;
    private String url;
    private String teamName;
    private Object notificationSettings; // Complex type, using Object for now
    private Object[] preferences; // Array of UserPreference objects
    private String createdOn;
    private Boolean twoFactorAuthEnabled;
    private Object[] tosAgreements; // Array of TermsOfServiceAgreement objects
  }
}
