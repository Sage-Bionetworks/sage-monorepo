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
 * LogoutResponseDto
 */

@JsonTypeName("LogoutResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LogoutResponseDto {

  private @Nullable String message;

  private @Nullable Integer revokedTokens;

  public LogoutResponseDto message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Logout success message
   * @return message
   */
  
  @Schema(name = "message", example = "Successfully logged out", description = "Logout success message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  public LogoutResponseDto revokedTokens(@Nullable Integer revokedTokens) {
    this.revokedTokens = revokedTokens;
    return this;
  }

  /**
   * Number of refresh tokens revoked
   * @return revokedTokens
   */
  
  @Schema(name = "revokedTokens", example = "1", description = "Number of refresh tokens revoked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revokedTokens")
  public @Nullable Integer getRevokedTokens() {
    return revokedTokens;
  }

  public void setRevokedTokens(@Nullable Integer revokedTokens) {
    this.revokedTokens = revokedTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogoutResponseDto logoutResponse = (LogoutResponseDto) o;
    return Objects.equals(this.message, logoutResponse.message) &&
        Objects.equals(this.revokedTokens, logoutResponse.revokedTokens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, revokedTokens);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogoutResponseDto {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    revokedTokens: ").append(toIndentedString(revokedTokens)).append("\n");
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

    private LogoutResponseDto instance;

    public Builder() {
      this(new LogoutResponseDto());
    }

    protected Builder(LogoutResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LogoutResponseDto value) { 
      this.instance.setMessage(value.message);
      this.instance.setRevokedTokens(value.revokedTokens);
      return this;
    }

    public LogoutResponseDto.Builder message(String message) {
      this.instance.message(message);
      return this;
    }
    
    public LogoutResponseDto.Builder revokedTokens(Integer revokedTokens) {
      this.instance.revokedTokens(revokedTokens);
      return this;
    }
    
    /**
    * returns a built LogoutResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LogoutResponseDto build() {
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
  public static LogoutResponseDto.Builder builder() {
    return new LogoutResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LogoutResponseDto.Builder toBuilder() {
    LogoutResponseDto.Builder builder = new LogoutResponseDto.Builder();
    return builder.copyOf(this);
  }

}

