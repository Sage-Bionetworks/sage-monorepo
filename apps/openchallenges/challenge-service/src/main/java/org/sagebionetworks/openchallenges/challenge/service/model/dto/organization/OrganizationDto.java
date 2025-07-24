package org.sagebionetworks.openchallenges.challenge.service.model.dto.organization;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

/**
 * A simplified representation of an organization for validation purposes
 */
@Data
public class OrganizationDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("login")
  private String login;

  @JsonProperty("description")
  private String description;

  @JsonProperty("createdAt")
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  private OffsetDateTime updatedAt;
}
