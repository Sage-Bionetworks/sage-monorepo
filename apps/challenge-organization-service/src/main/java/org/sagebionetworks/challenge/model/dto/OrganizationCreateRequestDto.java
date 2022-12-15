package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The information required to create an org account */
@Schema(
    name = "OrganizationCreateRequest",
    description = "The information required to create an org account")
@JsonTypeName("OrganizationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OrganizationCreateRequestDto {

  @JsonProperty("email")
  private String email;

  @JsonProperty("name")
  private String name;

  public OrganizationCreateRequestDto email(String email) {
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

  public OrganizationCreateRequestDto name(String name) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationCreateRequestDto organizationCreateRequest = (OrganizationCreateRequestDto) o;
    return Objects.equals(this.email, organizationCreateRequest.email)
        && Objects.equals(this.name, organizationCreateRequest.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationCreateRequestDto {\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
