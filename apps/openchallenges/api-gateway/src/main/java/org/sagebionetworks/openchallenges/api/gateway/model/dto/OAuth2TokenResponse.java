package org.sagebionetworks.openchallenges.api.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Response from OAuth2 token endpoint for client credentials flow.
 * Based on RFC 6749 Section 5.1.
 */
@Builder
public record OAuth2TokenResponse(
  @JsonProperty("access_token") String accessToken,

  @JsonProperty("token_type") String tokenType,

  @JsonProperty("expires_in") Integer expiresIn,

  @JsonProperty("scope") String scope
) {
  @Override
  public String toString() {
    return (
      "OAuth2TokenResponseDto{" +
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
