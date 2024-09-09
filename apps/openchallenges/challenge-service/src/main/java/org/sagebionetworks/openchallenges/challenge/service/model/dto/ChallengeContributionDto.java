package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** A challenge contribution. */
@Schema(name = "ChallengeContribution", description = "A challenge contribution.")
@JsonTypeName("ChallengeContribution")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeContributionDto {

  @JsonProperty("challengeId")
  private Long challengeId;

  @JsonProperty("organizationId")
  private Long organizationId;

  @JsonProperty("role")
  private ChallengeContributionRoleDto role;

  public ChallengeContributionDto challengeId(Long challengeId) {
    this.challengeId = challengeId;
    return this;
  }

  /**
   * The unique identifier of the challenge.
   *
   * @return challengeId
   */
  @NotNull
  @Schema(
    name = "challengeId",
    example = "1",
    description = "The unique identifier of the challenge.",
    required = true
  )
  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public ChallengeContributionDto organizationId(Long organizationId) {
    this.organizationId = organizationId;
    return this;
  }

  /**
   * The unique identifier of an organization
   *
   * @return organizationId
   */
  @NotNull
  @Schema(
    name = "organizationId",
    example = "1",
    description = "The unique identifier of an organization",
    required = true
  )
  public Long getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  public ChallengeContributionDto role(ChallengeContributionRoleDto role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   *
   * @return role
   */
  @NotNull
  @Valid
  @Schema(name = "role", required = true)
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
    ChallengeContributionDto challengeContribution = (ChallengeContributionDto) o;
    return (
      Objects.equals(this.challengeId, challengeContribution.challengeId) &&
      Objects.equals(this.organizationId, challengeContribution.organizationId) &&
      Objects.equals(this.role, challengeContribution.role)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(challengeId, organizationId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeContributionDto {\n");
    sb.append("    challengeId: ").append(toIndentedString(challengeId)).append("\n");
    sb.append("    organizationId: ").append(toIndentedString(organizationId)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
