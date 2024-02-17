package org.sagebionetworks.openchallenges.organization.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

/** An organization */
@Schema(name = "Organization", description = "An organization")
@JsonTypeName("Organization")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OrganizationDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("login")
  private String login;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("avatarKey")
  private String avatarKey;

  @JsonProperty("websiteUrl")
  private String websiteUrl = null;

  @JsonProperty("challengeCount")
  private Integer challengeCount = 0;

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  @JsonProperty("acronym")
  private String acronym;

  public OrganizationDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of an organization
   *
   * @return id
   */
  @NotNull
  @Schema(
      name = "id",
      example = "1",
      description = "The unique identifier of an organization",
      required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OrganizationDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the organization.
   *
   * @return name
   */
  @NotNull
  @Schema(
      name = "name",
      example = "Example organization",
      description = "The name of the organization.",
      required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OrganizationDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * The unique login of an organization.
   *
   * @return login
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 2, max = 64)
  @Schema(
      name = "login",
      example = "example-org",
      description = "The unique login of an organization.",
      required = true)
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public OrganizationDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A description of the organization.
   *
   * @return description
   */
  @Schema(
      name = "description",
      example = "A description of the organization.",
      description = "A description of the organization.",
      required = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OrganizationDto avatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
    return this;
  }

  /**
   * Get avatarKey
   *
   * @return avatarKey
   */
  @Schema(name = "avatarKey", example = "logo/dream.png", required = false)
  public String getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
  }

  public OrganizationDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   *
   * @return websiteUrl
   */
  @Size(max = 500)
  @Schema(
      name = "websiteUrl",
      example = "https://openchallenges.io",
      description = "A URL to the website or image.",
      required = false)
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public OrganizationDto challengeCount(Integer challengeCount) {
    this.challengeCount = challengeCount;
    return this;
  }

  /**
   * The number of challenges involving this organization. minimum: 0
   *
   * @return challengeCount
   */
  @Min(0)
  @Schema(
      name = "challengeCount",
      example = "10",
      description = "The number of challenges involving this organization.",
      required = false)
  public Integer getChallengeCount() {
    return challengeCount;
  }

  public void setChallengeCount(Integer challengeCount) {
    this.challengeCount = challengeCount;
  }

  public OrganizationDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Datetime when the object was added to the database.
   *
   * @return createdAt
   */
  @NotNull
  @Valid
  @Schema(
      name = "createdAt",
      example = "2022-07-04T22:19:11Z",
      description = "Datetime when the object was added to the database.",
      required = true)
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OrganizationDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Datetime when the object was last modified in the database.
   *
   * @return updatedAt
   */
  @NotNull
  @Valid
  @Schema(
      name = "updatedAt",
      example = "2022-07-04T22:19:11Z",
      description = "Datetime when the object was last modified in the database.",
      required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public OrganizationDto acronym(String acronym) {
    this.acronym = acronym;
    return this;
  }

  /**
   * An acronym of the organization.
   *
   * @return acronym
   */
  @Size(max = 10)
  @Schema(
      name = "acronym",
      example = "OC",
      description = "An acronym of the organization.",
      required = false)
  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationDto organization = (OrganizationDto) o;
    return Objects.equals(this.id, organization.id)
        && Objects.equals(this.name, organization.name)
        && Objects.equals(this.login, organization.login)
        && Objects.equals(this.description, organization.description)
        && Objects.equals(this.avatarKey, organization.avatarKey)
        && Objects.equals(this.websiteUrl, organization.websiteUrl)
        && Objects.equals(this.challengeCount, organization.challengeCount)
        && Objects.equals(this.createdAt, organization.createdAt)
        && Objects.equals(this.updatedAt, organization.updatedAt)
        && Objects.equals(this.acronym, organization.acronym);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        name,
        login,
        description,
        avatarKey,
        websiteUrl,
        challengeCount,
        createdAt,
        updatedAt,
        acronym);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatarKey: ").append(toIndentedString(avatarKey)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    challengeCount: ").append(toIndentedString(challengeCount)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    acronym: ").append(toIndentedString(acronym)).append("\n");
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
