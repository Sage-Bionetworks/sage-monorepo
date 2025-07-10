package org.sagebionetworks.openchallenges.challenge.service.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for API key validation from the auth service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateApiKeyResponseDto {

  @JsonProperty("valid")
  private Boolean valid;

  @JsonProperty("userId")
  private UUID userId;

  @JsonProperty("username")
  private String username;

  @JsonProperty("role")
  private String role;

  @JsonProperty("scopes")
  private List<String> scopes;
}
