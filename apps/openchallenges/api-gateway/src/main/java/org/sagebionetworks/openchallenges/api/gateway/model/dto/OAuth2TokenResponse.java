package org.sagebionetworks.openchallenges.api.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Response from OAuth2 token endpoint for client credentials flow.
 * Based on RFC 6749 Section 5.1.
 */
@Value
@Builder
@Jacksonized
public class OAuth2TokenResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("expires_in")
  private Integer expiresIn;

  @JsonProperty("scope")
  private String scope;

  @Override
  public String toString() {
    return (
      "OAuth2TokenResponse{" +
      "accessToken='[REDACTED]'" +
      ", tokenType='" +
      tokenType +
      '\'' +
      ", expiresIn=" +
      expiresIn +
      ", scope='" +
      scope +
      '\'' +
      '}'
    );
  }
}
