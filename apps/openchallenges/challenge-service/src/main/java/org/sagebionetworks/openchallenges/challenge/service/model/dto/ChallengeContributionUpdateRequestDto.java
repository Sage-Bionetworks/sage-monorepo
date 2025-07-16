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
 * A challenge contribution update request.
 */

@Schema(name = "ChallengeContributionUpdateRequest", description = "A challenge contribution update request.")
@JsonTypeName("ChallengeContributionUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeContributionUpdateRequestDto {

  private Long organizationId;

  private ChallengeContributionRoleDto role;

  public ChallengeContributionUpdateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeContributionUpdateRequestDto(Long organizationId, ChallengeContributionRoleDto role) {
    this.organizationId = organizationId;
    this.role = role;
  }

  public ChallengeContributionUpdateRequestDto organizationId(Long organizationId) {
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

  public ChallengeContributionUpdateRequestDto role(ChallengeContributionRoleDto role) {
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
    ChallengeContributionUpdateRequestDto challengeContributionUpdateRequest = (ChallengeContributionUpdateRequestDto) o;
    return Objects.equals(this.organizationId, challengeContributionUpdateRequest.organizationId) &&
        Objects.equals(this.role, challengeContributionUpdateRequest.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organizationId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeContributionUpdateRequestDto {\n");
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

    private ChallengeContributionUpdateRequestDto instance;

    public Builder() {
      this(new ChallengeContributionUpdateRequestDto());
    }

    protected Builder(ChallengeContributionUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeContributionUpdateRequestDto value) { 
      this.instance.setOrganizationId(value.organizationId);
      this.instance.setRole(value.role);
      return this;
    }

    public ChallengeContributionUpdateRequestDto.Builder organizationId(Long organizationId) {
      this.instance.organizationId(organizationId);
      return this;
    }
    
    public ChallengeContributionUpdateRequestDto.Builder role(ChallengeContributionRoleDto role) {
      this.instance.role(role);
      return this;
    }
    
    /**
    * returns a built ChallengeContributionUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeContributionUpdateRequestDto build() {
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
  public static ChallengeContributionUpdateRequestDto.Builder builder() {
    return new ChallengeContributionUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeContributionUpdateRequestDto.Builder toBuilder() {
    ChallengeContributionUpdateRequestDto.Builder builder = new ChallengeContributionUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

