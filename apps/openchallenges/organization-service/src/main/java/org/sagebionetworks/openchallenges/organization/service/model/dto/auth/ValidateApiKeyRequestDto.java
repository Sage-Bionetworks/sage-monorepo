package org.sagebionetworks.openchallenges.organization.service.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for validating API keys with the auth service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateApiKeyRequestDto {

  @JsonProperty("apiKey")
  private String apiKey;
}
