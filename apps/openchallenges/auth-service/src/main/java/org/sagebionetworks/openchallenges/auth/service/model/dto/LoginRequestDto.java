package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LoginRequestDto
 */

@JsonTypeName("LoginRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LoginRequestDto {

  private String username;

  private String password;

  public LoginRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LoginRequestDto(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public LoginRequestDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Username or email
   * @return username
   */
  @NotNull 
  @Schema(name = "username", example = "admin", description = "Username or email", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LoginRequestDto password(String password) {
    this.password = password;
    return this;
  }

  /**
   * User password
   * @return password
   */
  @NotNull 
  @Schema(name = "password", example = "changeme", description = "User password", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginRequestDto loginRequest = (LoginRequestDto) o;
    return Objects.equals(this.username, loginRequest.username) &&
        Objects.equals(this.password, loginRequest.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginRequestDto {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append("*").append("\n");
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

    private LoginRequestDto instance;

    public Builder() {
      this(new LoginRequestDto());
    }

    protected Builder(LoginRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LoginRequestDto value) { 
      this.instance.setUsername(value.username);
      this.instance.setPassword(value.password);
      return this;
    }

    public LoginRequestDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public LoginRequestDto.Builder password(String password) {
      this.instance.password(password);
      return this;
    }
    
    /**
    * returns a built LoginRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LoginRequestDto build() {
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
  public static LoginRequestDto.Builder builder() {
    return new LoginRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LoginRequestDto.Builder toBuilder() {
    LoginRequestDto.Builder builder = new LoginRequestDto.Builder();
    return builder.copyOf(this);
  }

}

