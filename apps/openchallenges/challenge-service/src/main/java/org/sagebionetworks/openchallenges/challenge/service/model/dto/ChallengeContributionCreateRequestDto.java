package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A challenge contribution to be created.
 */

@Schema(name = "ChallengeContributionCreateRequest", description = "A challenge contribution to be created.")
@JsonTypeName("ChallengeContributionCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeContributionCreateRequestDto {

  private Long organizationId;

  private ChallengeContributionRoleDto role;

  public ChallengeContributionCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeContributionCreateRequestDto(Long organizationId, ChallengeContributionRoleDto role) {
    this.organizationId = organizationId;
    this.role = role;
  }

  public ChallengeContributionCreateRequestDto organizationId(Long organizationId) {
    this.organizationId = organizationId;
    return this;
  }

  /**
   * The unique identifier of an organization
   * @return organizationId
   */
  @NotNull 
  @Schema(name = "organizationId", example = "1", description = "The unique identifier of an organization", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("organizationId")
  public Long getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  public ChallengeContributionCreateRequestDto role(ChallengeContributionRoleDto role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   * @return role
   */
  @NotNull @Valid 
  @Schema(name = "role", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("role")
  public ChallengeContributionRoleDto getRole() {
    return role;
  }

  public void setRole(ChallengeContributionRoleDto role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeContributionCreateRequestDto challengeContributionCreateRequest = (ChallengeContributionCreateRequestDto) o;
    return Objects.equals(this.organizationId, challengeContributionCreateRequest.organizationId) &&
        Objects.equals(this.role, challengeContributionCreateRequest.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organizationId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeContributionCreateRequestDto {\n");
    sb.append("    organizationId: ").append(toIndentedString(organizationId)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public static class Builder {

    private ChallengeContributionCreateRequestDto instance;

    public Builder() {
      this(new ChallengeContributionCreateRequestDto());
    }

    protected Builder(ChallengeContributionCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeContributionCreateRequestDto value) { 
      this.instance.setOrganizationId(value.organizationId);
      this.instance.setRole(value.role);
      return this;
    }

    public ChallengeContributionCreateRequestDto.Builder organizationId(Long organizationId) {
      this.instance.organizationId(organizationId);
      return this;
    }
    
    public ChallengeContributionCreateRequestDto.Builder role(ChallengeContributionRoleDto role) {
      this.instance.role(role);
      return this;
    }
    
    /**
    * returns a built ChallengeContributionCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeContributionCreateRequestDto build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
  * Create a builder with no initialized field (except for the default values).
  */
  public static ChallengeContributionCreateRequestDto.Builder builder() {
    return new ChallengeContributionCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeContributionCreateRequestDto.Builder toBuilder() {
    ChallengeContributionCreateRequestDto.Builder builder = new ChallengeContributionCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

