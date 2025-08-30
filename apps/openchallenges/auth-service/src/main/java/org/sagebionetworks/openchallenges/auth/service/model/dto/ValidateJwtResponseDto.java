package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ValidateJwtResponseDto
 */

@JsonTypeName("ValidateJwtResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ValidateJwtResponseDto {

  private @Nullable Boolean valid;

  private @Nullable UUID userId;

  private @Nullable String username;

  /**
   * User role from token (if valid)
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

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt;

  public ValidateJwtResponseDto valid(@Nullable Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Whether the JWT token is valid
   * @return valid
   */
  
  @Schema(name = "valid", example = "true", description = "Whether the JWT token is valid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valid")
  public @Nullable Boolean getValid() {
    return valid;
  }

  public void setValid(@Nullable Boolean valid) {
    this.valid = valid;
  }

  public ValidateJwtResponseDto userId(@Nullable UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * User ID from token (if valid)
   * @return userId
   */
  @Valid 
  @Schema(name = "userId", example = "123e4567-e89b-12d3-a456-426614174000", description = "User ID from token (if valid)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public @Nullable UUID getUserId() {
    return userId;
  }

  public void setUserId(@Nullable UUID userId) {
    this.userId = userId;
  }

  public ValidateJwtResponseDto username(@Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Username from token (if valid)
   * @return username
   */
  
  @Schema(name = "username", example = "admin", description = "Username from token (if valid)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public @Nullable String getUsername() {
    return username;
  }

  public void setUsername(@Nullable String username) {
    this.username = username;
  }

  public ValidateJwtResponseDto role(@Nullable RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * User role from token (if valid)
   * @return role
   */
  
  @Schema(name = "role", example = "admin", description = "User role from token (if valid)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public @Nullable RoleEnum getRole() {
    return role;
  }

  public void setRole(@Nullable RoleEnum role) {
    this.role = role;
  }

  public ValidateJwtResponseDto expiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  /**
   * Token expiration time (if valid)
   * @return expiresAt
   */
  @Valid 
  @Schema(name = "expiresAt", example = "2025-08-30T15:30Z", description = "Token expiration time (if valid)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresAt")
  public @Nullable OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidateJwtResponseDto validateJwtResponse = (ValidateJwtResponseDto) o;
    return Objects.equals(this.valid, validateJwtResponse.valid) &&
        Objects.equals(this.userId, validateJwtResponse.userId) &&
        Objects.equals(this.username, validateJwtResponse.username) &&
        Objects.equals(this.role, validateJwtResponse.role) &&
        Objects.equals(this.expiresAt, validateJwtResponse.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valid, userId, username, role, expiresAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidateJwtResponseDto {\n");
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
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

    private ValidateJwtResponseDto instance;

    public Builder() {
      this(new ValidateJwtResponseDto());
    }

    protected Builder(ValidateJwtResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ValidateJwtResponseDto value) { 
      this.instance.setValid(value.valid);
      this.instance.setUserId(value.userId);
      this.instance.setUsername(value.username);
      this.instance.setRole(value.role);
      this.instance.setExpiresAt(value.expiresAt);
      return this;
    }

    public ValidateJwtResponseDto.Builder valid(Boolean valid) {
      this.instance.valid(valid);
      return this;
    }
    
    public ValidateJwtResponseDto.Builder userId(UUID userId) {
      this.instance.userId(userId);
      return this;
    }
    
    public ValidateJwtResponseDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public ValidateJwtResponseDto.Builder role(RoleEnum role) {
      this.instance.role(role);
      return this;
    }
    
    public ValidateJwtResponseDto.Builder expiresAt(OffsetDateTime expiresAt) {
      this.instance.expiresAt(expiresAt);
      return this;
    }
    
    /**
    * returns a built ValidateJwtResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ValidateJwtResponseDto build() {
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
  public static ValidateJwtResponseDto.Builder builder() {
    return new ValidateJwtResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ValidateJwtResponseDto.Builder toBuilder() {
    ValidateJwtResponseDto.Builder builder = new ValidateJwtResponseDto.Builder();
    return builder.copyOf(this);
  }

}

