package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.openapitools.jackson.nullable.JsonNullable;

/** The information required to create a user account */
@Schema(
  name = "UserCreateRequest",
  description = "The information required to create a user account"
)
@JsonTypeName("UserCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class UserCreateRequestDto {

  @JsonProperty("login")
  private String login;

  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("name")
  private JsonNullable<String> name = JsonNullable.undefined();

  @JsonProperty("avatarUrl")
  private JsonNullable<String> avatarUrl = JsonNullable.undefined();

  @JsonProperty("bio")
  private JsonNullable<String> bio = JsonNullable.undefined();

  public UserCreateRequestDto login(String login) {
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

  public UserCreateRequestDto email(String email) {
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

  public UserCreateRequestDto password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   *
   * @return password
   */
  @NotNull
  @Schema(name = "password", required = true)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserCreateRequestDto name(String name) {
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

  public UserCreateRequestDto avatarUrl(String avatarUrl) {
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

  public UserCreateRequestDto bio(String bio) {
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
    UserCreateRequestDto userCreateRequest = (UserCreateRequestDto) o;
    return (
      Objects.equals(this.login, userCreateRequest.login) &&
      Objects.equals(this.email, userCreateRequest.email) &&
      Objects.equals(this.password, userCreateRequest.password) &&
      Objects.equals(this.name, userCreateRequest.name) &&
      Objects.equals(this.avatarUrl, userCreateRequest.avatarUrl) &&
      Objects.equals(this.bio, userCreateRequest.bio)
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
    return Objects.hash(login, email, password, name, avatarUrl, bio);
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
    sb.append("class UserCreateRequestDto {\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
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
