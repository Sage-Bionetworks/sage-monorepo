package org.sagebionetworks.openchallenges.challenge.service.model.dto.organization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Objects;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationRoleDto;
import org.springframework.lang.Nullable;

/**
 * A request to create a challenge participation for an organization, defined by a challenge ID and a role.
 */

@Schema(
  name = "ChallengeParticipationCreateRequest",
  description = "A request to create a challenge participation for an organization, defined by a challenge ID and a role. "
)
@JsonTypeName("ChallengeParticipationCreateRequest")
@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
public class ChallengeParticipationCreateRequestDto {

  private Long challengeId;

  private ChallengeParticipationRoleDto role;

  public ChallengeParticipationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeParticipationCreateRequestDto(
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    this.challengeId = challengeId;
    this.role = role;
  }

  public ChallengeParticipationCreateRequestDto challengeId(Long challengeId) {
    this.challengeId = challengeId;
    return this;
  }

  /**
   * The unique identifier of the challenge.
   * @return challengeId
   */
  @NotNull
  @Schema(
    name = "challengeId",
    example = "1",
    description = "The unique identifier of the challenge.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @JsonProperty("challengeId")
  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public ChallengeParticipationCreateRequestDto role(ChallengeParticipationRoleDto role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   * @return role
   */
  @NotNull
  @Valid
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
    ChallengeParticipationCreateRequestDto challengeParticipationCreateRequest =
      (ChallengeParticipationCreateRequestDto) o;
    return (
      Objects.equals(this.challengeId, challengeParticipationCreateRequest.challengeId) &&
      Objects.equals(this.role, challengeParticipationCreateRequest.role)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(challengeId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeParticipationCreateRequestDto {\n");
    sb.append("    challengeId: ").append(toIndentedString(challengeId)).append("\n");
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

    private ChallengeParticipationCreateRequestDto instance;

    public Builder() {
      this(new ChallengeParticipationCreateRequestDto());
    }

    protected Builder(ChallengeParticipationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeParticipationCreateRequestDto value) {
      this.instance.setChallengeId(value.challengeId);
      this.instance.setRole(value.role);
      return this;
    }

    public ChallengeParticipationCreateRequestDto.Builder challengeId(Long challengeId) {
      this.instance.challengeId(challengeId);
      return this;
    }

    public ChallengeParticipationCreateRequestDto.Builder role(ChallengeParticipationRoleDto role) {
      this.instance.role(role);
      return this;
    }

    /**
     * returns a built ChallengeParticipationCreateRequestDto instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public ChallengeParticipationCreateRequestDto build() {
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
  public static ChallengeParticipationCreateRequestDto.Builder builder() {
    return new ChallengeParticipationCreateRequestDto.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public ChallengeParticipationCreateRequestDto.Builder toBuilder() {
    ChallengeParticipationCreateRequestDto.Builder builder =
      new ChallengeParticipationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }
}
