package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LoginResponseDto
 */

@JsonTypeName("LoginResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LoginResponseDto {

  private @Nullable String apiKey;

  private @Nullable UUID userId;

  private @Nullable String username;

  /**
   * User role
   */
  public enum RoleEnum {
    ADMIN("admin"),
    
    USER("user"),
    
    READONLY("readonly"),
    
    SERVICE("service");

    private final String value;

    RoleEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RoleEnum fromValue(String value) {
      for (RoleEnum b : RoleEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable RoleEnum role;

  public LoginResponseDto apiKey(@Nullable String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  /**
   * API key for authentication
   * @return apiKey
   */
  
  @Schema(name = "apiKey", example = "oc_prod_abcd1234567890abcdef1234567890abcdef1234", description = "API key for authentication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("apiKey")
  public @Nullable String getApiKey() {
    return apiKey;
  }

  public void setApiKey(@Nullable String apiKey) {
    this.apiKey = apiKey;
  }

  public LoginResponseDto userId(@Nullable UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * User ID
   * @return userId
   */
  @Valid 
  @Schema(name = "userId", example = "123e4567-e89b-12d3-a456-426614174000", description = "User ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public @Nullable UUID getUserId() {
    return userId;
  }

  public void setUserId(@Nullable UUID userId) {
    this.userId = userId;
  }

  public LoginResponseDto username(@Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Username
   * @return username
   */
  
  @Schema(name = "username", example = "admin", description = "Username", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public @Nullable String getUsername() {
    return username;
  }

  public void setUsername(@Nullable String username) {
    this.username = username;
  }

  public LoginResponseDto role(@Nullable RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * User role
   * @return role
   */
  
  @Schema(name = "role", example = "admin", description = "User role", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public @Nullable RoleEnum getRole() {
    return role;
  }

  public void setRole(@Nullable RoleEnum role) {
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
    LoginResponseDto loginResponse = (LoginResponseDto) o;
    return Objects.equals(this.apiKey, loginResponse.apiKey) &&
        Objects.equals(this.userId, loginResponse.userId) &&
        Objects.equals(this.username, loginResponse.username) &&
        Objects.equals(this.role, loginResponse.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKey, userId, username, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginResponseDto {\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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

    private LoginResponseDto instance;

    public Builder() {
      this(new LoginResponseDto());
    }

    protected Builder(LoginResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LoginResponseDto value) { 
      this.instance.setApiKey(value.apiKey);
      this.instance.setUserId(value.userId);
      this.instance.setUsername(value.username);
      this.instance.setRole(value.role);
      return this;
    }

    public LoginResponseDto.Builder apiKey(String apiKey) {
      this.instance.apiKey(apiKey);
      return this;
    }
    
    public LoginResponseDto.Builder userId(UUID userId) {
      this.instance.userId(userId);
      return this;
    }
    
    public LoginResponseDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public LoginResponseDto.Builder role(RoleEnum role) {
      this.instance.role(role);
      return this;
    }
    
    /**
    * returns a built LoginResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LoginResponseDto build() {
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
  public static LoginResponseDto.Builder builder() {
    return new LoginResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LoginResponseDto.Builder toBuilder() {
    LoginResponseDto.Builder builder = new LoginResponseDto.Builder();
    return builder.copyOf(this);
  }

}

