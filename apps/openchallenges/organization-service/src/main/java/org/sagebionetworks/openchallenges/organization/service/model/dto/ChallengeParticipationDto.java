package org.sagebionetworks.openchallenges.organization.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An challenge participation.
 */

@Schema(name = "ChallengeParticipation", description = "An challenge participation.")
@JsonTypeName("ChallengeParticipation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ChallengeParticipationDto {

  private Long id;

  private Long challengeId;

  private Long organizationId;

  private ChallengeParticipationRoleDto role;

  public ChallengeParticipationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeParticipationDto(Long id, Long challengeId, Long organizationId, ChallengeParticipationRoleDto role) {
    this.id = id;
    this.challengeId = challengeId;
    this.organizationId = organizationId;
    this.role = role;
  }

  public ChallengeParticipationDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge participation
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of a challenge participation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengeParticipationDto challengeId(Long challengeId) {
    this.challengeId = challengeId;
    return this;
  }

  /**
   * The unique identifier of the challenge.
   * @return challengeId
   */
  @NotNull 
  @Schema(name = "challengeId", example = "1", description = "The unique identifier of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("challengeId")
  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public ChallengeParticipationDto organizationId(Long organizationId) {
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

  public ChallengeParticipationDto role(ChallengeParticipationRoleDto role) {
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
  public ChallengeParticipationRoleDto getRole() {
    return role;
  }

  public void setRole(ChallengeParticipationRoleDto role) {
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
    ChallengeParticipationDto challengeParticipation = (ChallengeParticipationDto) o;
    return Objects.equals(this.id, challengeParticipation.id) &&
        Objects.equals(this.challengeId, challengeParticipation.challengeId) &&
        Objects.equals(this.organizationId, challengeParticipation.organizationId) &&
        Objects.equals(this.role, challengeParticipation.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, challengeId, organizationId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeParticipationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    challengeId: ").append(toIndentedString(challengeId)).append("\n");
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

    private ChallengeParticipationDto instance;

    public Builder() {
      this(new ChallengeParticipationDto());
    }

    protected Builder(ChallengeParticipationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeParticipationDto value) { 
      this.instance.setId(value.id);
      this.instance.setChallengeId(value.challengeId);
      this.instance.setOrganizationId(value.organizationId);
      this.instance.setRole(value.role);
      return this;
    }

    public ChallengeParticipationDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public ChallengeParticipationDto.Builder challengeId(Long challengeId) {
      this.instance.challengeId(challengeId);
      return this;
    }
    
    public ChallengeParticipationDto.Builder organizationId(Long organizationId) {
      this.instance.organizationId(organizationId);
      return this;
    }
    
    public ChallengeParticipationDto.Builder role(ChallengeParticipationRoleDto role) {
      this.instance.role(role);
      return this;
    }
    
    /**
    * returns a built ChallengeParticipationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeParticipationDto build() {
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
  public static ChallengeParticipationDto.Builder builder() {
    return new ChallengeParticipationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeParticipationDto.Builder toBuilder() {
    ChallengeParticipationDto.Builder builder = new ChallengeParticipationDto.Builder();
    return builder.copyOf(this);
  }

}

