package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;

/** A simple user */
@Schema(name = "User", description = "A simple user")
@JsonTypeName("User")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class UserDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("login")
  private String login;

  @JsonProperty("email")
  private String email;

  @JsonProperty("name")
  private JsonNullable<String> name = JsonNullable.undefined();

  @JsonProperty("status")
  private UserStatusDto status;

  @JsonProperty("avatarUrl")
  private JsonNullable<String> avatarUrl = JsonNullable.undefined();

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  @JsonProperty("type")
  private String type;

  @JsonProperty("bio")
  private JsonNullable<String> bio = JsonNullable.undefined();

  public UserDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of an account
   *
   * @return id
   */
  @Schema(
    name = "id",
    example = "1",
    description = "The unique identifier of an account",
    required = false
  )
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * Get login
   *
   * @return login
   */
  @NotNull
  @Schema(name = "login", required = true)
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public UserDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * An email address
   *
   * @return email
   */
  @NotNull
  @Email
  @Schema(
    name = "email",
    example = "john.smith@example.com",
    description = "An email address",
    required = true
  )
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserDto name(String name) {
    this.name = JsonNullable.of(name);
    return this;
  }

  /**
   * Get name
   *
   * @return name
   */
  @Schema(name = "name", required = false)
  public JsonNullable<String> getName() {
    return name;
  }

  public void setName(JsonNullable<String> name) {
    this.name = name;
  }

  public UserDto status(UserStatusDto status) {
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
  public UserStatusDto getStatus() {
    return status;
  }

  public void setStatus(UserStatusDto status) {
    this.status = status;
  }

  public UserDto avatarUrl(String avatarUrl) {
    this.avatarUrl = JsonNullable.of(avatarUrl);
    return this;
  }

  /**
   * Get avatarUrl
   *
   * @return avatarUrl
   */
  @Schema(name = "avatarUrl", example = "https://example.com/awesome-avatar.png", required = false)
  public JsonNullable<String> getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(JsonNullable<String> avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public UserDto createdAt(OffsetDateTime createdAt) {
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
  @Schema(name = "createdAt", required = true)
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public UserDto updatedAt(OffsetDateTime updatedAt) {
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
  @Schema(name = "updatedAt", required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UserDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  @NotNull
  @Schema(name = "type", example = "User", required = true)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UserDto bio(String bio) {
    this.bio = JsonNullable.of(bio);
    return this;
  }

  /**
   * Get bio
   *
   * @return bio
   */
  @Schema(name = "bio", required = false)
  public JsonNullable<String> getBio() {
    return bio;
  }

  public void setBio(JsonNullable<String> bio) {
    this.bio = bio;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDto user = (UserDto) o;
    return (
      Objects.equals(this.id, user.id) &&
      Objects.equals(this.login, user.login) &&
      Objects.equals(this.email, user.email) &&
      Objects.equals(this.name, user.name) &&
      Objects.equals(this.status, user.status) &&
      Objects.equals(this.avatarUrl, user.avatarUrl) &&
      Objects.equals(this.createdAt, user.createdAt) &&
      Objects.equals(this.updatedAt, user.updatedAt) &&
      Objects.equals(this.type, user.type) &&
      Objects.equals(this.bio, user.bio)
    );
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return (
      a == b ||
      (a != null &&
        b != null &&
        a.isPresent() &&
        b.isPresent() &&
        Objects.deepEquals(a.get(), b.get()))
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, login, email, name, status, avatarUrl, createdAt, updatedAt, type, bio);
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[] { a.get() }) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
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
