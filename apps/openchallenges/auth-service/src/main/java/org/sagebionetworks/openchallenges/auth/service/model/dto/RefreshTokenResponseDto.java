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
 * RefreshTokenResponseDto
 */

@JsonTypeName("RefreshTokenResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class RefreshTokenResponseDto {

  private @Nullable String accessToken;

  private String tokenType = "Bearer";

  private @Nullable Integer expiresIn;

  public RefreshTokenResponseDto accessToken(@Nullable String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * New JWT access token
   * @return accessToken
   */
  
  @Schema(name = "accessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "New JWT access token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessToken")
  public @Nullable String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(@Nullable String accessToken) {
    this.accessToken = accessToken;
  }

  public RefreshTokenResponseDto tokenType(String tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  /**
   * Token type
   * @return tokenType
   */
  
  @Schema(name = "tokenType", example = "Bearer", description = "Token type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tokenType")
  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public RefreshTokenResponseDto expiresIn(@Nullable Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Access token expiry time in seconds
   * @return expiresIn
   */
  
  @Schema(name = "expiresIn", example = "3600", description = "Access token expiry time in seconds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresIn")
  public @Nullable Integer getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(@Nullable Integer expiresIn) {
    this.expiresIn = expiresIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefreshTokenResponseDto refreshTokenResponse = (RefreshTokenResponseDto) o;
    return Objects.equals(this.accessToken, refreshTokenResponse.accessToken) &&
        Objects.equals(this.tokenType, refreshTokenResponse.tokenType) &&
        Objects.equals(this.expiresIn, refreshTokenResponse.expiresIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, tokenType, expiresIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefreshTokenResponseDto {\n");
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
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

    private RefreshTokenResponseDto instance;

    public Builder() {
      this(new RefreshTokenResponseDto());
    }

    protected Builder(RefreshTokenResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RefreshTokenResponseDto value) { 
      this.instance.setAccessToken(value.accessToken);
      this.instance.setTokenType(value.tokenType);
      this.instance.setExpiresIn(value.expiresIn);
      return this;
    }

    public RefreshTokenResponseDto.Builder accessToken(String accessToken) {
      this.instance.accessToken(accessToken);
      return this;
    }
    
    public RefreshTokenResponseDto.Builder tokenType(String tokenType) {
      this.instance.tokenType(tokenType);
      return this;
    }
    
    public RefreshTokenResponseDto.Builder expiresIn(Integer expiresIn) {
      this.instance.expiresIn(expiresIn);
      return this;
    }
    
    /**
    * returns a built RefreshTokenResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RefreshTokenResponseDto build() {
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
  public static RefreshTokenResponseDto.Builder builder() {
    return new RefreshTokenResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RefreshTokenResponseDto.Builder toBuilder() {
    RefreshTokenResponseDto.Builder builder = new RefreshTokenResponseDto.Builder();
    return builder.copyOf(this);
  }

}

