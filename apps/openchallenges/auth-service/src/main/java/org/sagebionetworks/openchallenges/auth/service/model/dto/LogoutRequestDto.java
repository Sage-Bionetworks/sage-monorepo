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
 * LogoutRequestDto
 */

@JsonTypeName("LogoutRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LogoutRequestDto {

  private String refreshToken;

  private Boolean revokeAllTokens = false;

  public LogoutRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LogoutRequestDto(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public LogoutRequestDto refreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
    return this;
  }

  /**
   * JWT refresh token to revoke
   * @return refreshToken
   */
  @NotNull 
  @Schema(name = "refreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT refresh token to revoke", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("refreshToken")
  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public LogoutRequestDto revokeAllTokens(Boolean revokeAllTokens) {
    this.revokeAllTokens = revokeAllTokens;
    return this;
  }

  /**
   * Whether to revoke all refresh tokens for the user (default false)
   * @return revokeAllTokens
   */
  
  @Schema(name = "revokeAllTokens", example = "false", description = "Whether to revoke all refresh tokens for the user (default false)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revokeAllTokens")
  public Boolean getRevokeAllTokens() {
    return revokeAllTokens;
  }

  public void setRevokeAllTokens(Boolean revokeAllTokens) {
    this.revokeAllTokens = revokeAllTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogoutRequestDto logoutRequest = (LogoutRequestDto) o;
    return Objects.equals(this.refreshToken, logoutRequest.refreshToken) &&
        Objects.equals(this.revokeAllTokens, logoutRequest.revokeAllTokens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(refreshToken, revokeAllTokens);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogoutRequestDto {\n");
    sb.append("    refreshToken: ").append(toIndentedString(refreshToken)).append("\n");
    sb.append("    revokeAllTokens: ").append(toIndentedString(revokeAllTokens)).append("\n");
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

    private LogoutRequestDto instance;

    public Builder() {
      this(new LogoutRequestDto());
    }

    protected Builder(LogoutRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LogoutRequestDto value) { 
      this.instance.setRefreshToken(value.refreshToken);
      this.instance.setRevokeAllTokens(value.revokeAllTokens);
      return this;
    }

    public LogoutRequestDto.Builder refreshToken(String refreshToken) {
      this.instance.refreshToken(refreshToken);
      return this;
    }
    
    public LogoutRequestDto.Builder revokeAllTokens(Boolean revokeAllTokens) {
      this.instance.revokeAllTokens(revokeAllTokens);
      return this;
    }
    
    /**
    * returns a built LogoutRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LogoutRequestDto build() {
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
  public static LogoutRequestDto.Builder builder() {
    return new LogoutRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LogoutRequestDto.Builder toBuilder() {
    LogoutRequestDto.Builder builder = new LogoutRequestDto.Builder();
    return builder.copyOf(this);
  }

}

