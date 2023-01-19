package org.sagebionetworks.challenge.model.dto;

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

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;

  @JsonProperty("login")
  private String login;

  @JsonProperty("description")
  private String description;

  @JsonProperty("avatarUrl")
  private String avatarUrl = null;

  @JsonProperty("websiteUrl")
  private String websiteUrl;

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public OrganizationDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   *
   * @return name
   */
  @NotNull
  @Schema(name = "name", example = "Example organization", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OrganizationDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * An email address.
   *
   * @return email
   */
  @NotNull
  @Email
  @Schema(
      name = "email",
      example = "john.smith@example.com",
      description = "An email address.",
      required = true)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public OrganizationDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * The login of an organization
   *
   * @return login
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 25)
  @Schema(
      name = "login",
      example = "example-org",
      description = "The login of an organization",
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
   * Get description
   *
   * @return description
   */
  @NotNull
  @Schema(name = "description", example = "A description of the organization.", required = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OrganizationDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * Get avatarUrl
   *
   * @return avatarUrl
   */
  @NotNull
  @Schema(name = "avatarUrl", example = "https://via.placeholder.com/300.png", required = true)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public OrganizationDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * Get websiteUrl
   *
   * @return websiteUrl
   */
  @NotNull
  @Schema(name = "websiteUrl", example = "https://example.com", required = true)
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public OrganizationDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   *
   * @return createdAt
   */
  @NotNull
  @Valid
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", required = true)
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
   * Get updatedAt
   *
   * @return updatedAt
   */
  @NotNull
  @Valid
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
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
    return Objects.equals(this.name, organization.name)
        && Objects.equals(this.email, organization.email)
        && Objects.equals(this.login, organization.login)
        && Objects.equals(this.description, organization.description)
        && Objects.equals(this.avatarUrl, organization.avatarUrl)
        && Objects.equals(this.websiteUrl, organization.websiteUrl)
        && Objects.equals(this.createdAt, organization.createdAt)
        && Objects.equals(this.updatedAt, organization.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name, email, login, description, avatarUrl, websiteUrl, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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
