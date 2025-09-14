package org.sagebionetworks.openchallenges.api.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Request for OAuth2 token endpoint using client credentials flow.
 * Based on RFC 6749 Section 4.4.
 */
@Value
@Builder
@Jacksonized
public class OAuth2TokenRequestDto {

  @JsonProperty("grant_type")
  String grantType;

  @JsonProperty("scope")
  String scope;

  @JsonProperty("resource")
  String resource;

  @Override
  public String toString() {
    return (
      "OAuth2TokenRequestDto{" +
      "grantType='" +
      grantType +
      '\'' +
      ", scope='" +
      scope +
      '\'' +
      ", resource='" +
      resource +
      '\'' +
      '}'
    );
  }
}
