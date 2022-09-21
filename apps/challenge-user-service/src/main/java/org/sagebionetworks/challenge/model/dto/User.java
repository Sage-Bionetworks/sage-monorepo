package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** User */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class User {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("username")
  private String username;

  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("authId")
  private String authId;

  @JsonProperty("status")
  private UserStatus status;

  public User id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   *
   * @return id
   */
  @Schema(name = "id", required = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   *
   * @return username
   */
  @Schema(name = "username", required = false)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public User email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   *
   * @return email
   */
  @Schema(name = "email", required = false)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public User password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   *
   * @return password
   */
  @Schema(name = "password", required = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User authId(String authId) {
    this.authId = authId;
    return this;
  }

  /**
   * Get authId
   *
   * @return authId
   */
  @Schema(name = "authId", required = false)
  public String getAuthId() {
    return authId;
  }

  public void setAuthId(String authId) {
    this.authId = authId;
  }

  public User status(UserStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   */
  @Valid
  @Schema(name = "status", required = false)
  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.id, user.id)
        && Objects.equals(this.username, user.username)
        && Objects.equals(this.email, user.email)
        && Objects.equals(this.password, user.password)
        && Objects.equals(this.authId, user.authId)
        && Objects.equals(this.status, user.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, email, password, authId, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    authId: ").append(toIndentedString(authId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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
