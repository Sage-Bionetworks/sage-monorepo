package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** An organization */
@Schema(name = "Organization", description = "An organization")
@JsonTypeName("Organization")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OrganizationDto {

  @JsonProperty("email")
  private String email;

  @JsonProperty("login")
  private String login;

  @JsonProperty("name")
  private String name;

  @JsonProperty("avatarUrl")
  private String avatarUrl = null;

  @JsonProperty("id")
  private Long id;

  public OrganizationDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * An email address.
   *
   * @return email
   */
  @Email
  @Schema(
      name = "email",
      example = "john.smith@example.com",
      description = "An email address.",
      required = false)
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
   * Get login
   *
   * @return login
   */
  @Schema(name = "login", example = "example-organization", required = false)
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public OrganizationDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   *
   * @return name
   */
  @Schema(name = "name", example = "Example organization", required = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
  @Schema(name = "avatarUrl", example = "https://via.placeholder.com/300.png", required = false)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationDto organization = (OrganizationDto) o;
    return Objects.equals(this.email, organization.email)
        && Objects.equals(this.login, organization.login)
        && Objects.equals(this.name, organization.name)
        && Objects.equals(this.avatarUrl, organization.avatarUrl)
        && Objects.equals(this.id, organization.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, login, name, avatarUrl, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationDto {\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
