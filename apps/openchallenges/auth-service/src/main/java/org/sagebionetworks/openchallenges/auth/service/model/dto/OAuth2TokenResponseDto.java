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
 * OAuth2 access token response
 */

@Schema(name = "OAuth2TokenResponse", description = "OAuth2 access token response")
@JsonTypeName("OAuth2TokenResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OAuth2TokenResponseDto {

  private String accessToken;

  private String tokenType;

  private Integer expiresIn;

  private @Nullable String refreshToken;

  private @Nullable String scope;

  public OAuth2TokenResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OAuth2TokenResponseDto(String accessToken, String tokenType, Integer expiresIn) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
  }

  public OAuth2TokenResponseDto accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * The access token
   * @return accessToken
   */
  @NotNull 
  @Schema(name = "access_token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "The access token", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("access_token")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public OAuth2TokenResponseDto tokenType(String tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  /**
   * Token type, always 'Bearer'
   * @return tokenType
   */
  @NotNull 
  @Schema(name = "token_type", example = "Bearer", description = "Token type, always 'Bearer'", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token_type")
  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public OAuth2TokenResponseDto expiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Token lifetime in seconds
   * @return expiresIn
   */
  @NotNull 
  @Schema(name = "expires_in", example = "3600", description = "Token lifetime in seconds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expires_in")
  public Integer getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
  }

  public OAuth2TokenResponseDto refreshToken(@Nullable String refreshToken) {
    this.refreshToken = refreshToken;
    return this;
  }

  /**
   * Refresh token for obtaining new access tokens
   * @return refreshToken
   */
  
  @Schema(name = "refresh_token", example = "def456...", description = "Refresh token for obtaining new access tokens", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refresh_token")
  public @Nullable String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(@Nullable String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public OAuth2TokenResponseDto scope(@Nullable String scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Space-separated list of granted scopes
   * @return scope
   */
  
  @Schema(name = "scope", example = "read:org write:org user:profile", description = "Space-separated list of granted scopes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scope")
  public @Nullable String getScope() {
    return scope;
  }

  public void setScope(@Nullable String scope) {
    this.scope = scope;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OAuth2TokenResponseDto oauth2TokenResponse = (OAuth2TokenResponseDto) o;
    return Objects.equals(this.accessToken, oauth2TokenResponse.accessToken) &&
        Objects.equals(this.tokenType, oauth2TokenResponse.tokenType) &&
        Objects.equals(this.expiresIn, oauth2TokenResponse.expiresIn) &&
        Objects.equals(this.refreshToken, oauth2TokenResponse.refreshToken) &&
        Objects.equals(this.scope, oauth2TokenResponse.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, tokenType, expiresIn, refreshToken, scope);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OAuth2TokenResponseDto {\n");
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
    sb.append("    refreshToken: ").append(toIndentedString(refreshToken)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
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

    private OAuth2TokenResponseDto instance;

    public Builder() {
      this(new OAuth2TokenResponseDto());
    }

    protected Builder(OAuth2TokenResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OAuth2TokenResponseDto value) { 
      this.instance.setAccessToken(value.accessToken);
      this.instance.setTokenType(value.tokenType);
      this.instance.setExpiresIn(value.expiresIn);
      this.instance.setRefreshToken(value.refreshToken);
      this.instance.setScope(value.scope);
      return this;
    }

    public OAuth2TokenResponseDto.Builder accessToken(String accessToken) {
      this.instance.accessToken(accessToken);
      return this;
    }
    
    public OAuth2TokenResponseDto.Builder tokenType(String tokenType) {
      this.instance.tokenType(tokenType);
      return this;
    }
    
    public OAuth2TokenResponseDto.Builder expiresIn(Integer expiresIn) {
      this.instance.expiresIn(expiresIn);
      return this;
    }
    
    public OAuth2TokenResponseDto.Builder refreshToken(String refreshToken) {
      this.instance.refreshToken(refreshToken);
      return this;
    }
    
    public OAuth2TokenResponseDto.Builder scope(String scope) {
      this.instance.scope(scope);
      return this;
    }
    
    /**
    * returns a built OAuth2TokenResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OAuth2TokenResponseDto build() {
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
  public static OAuth2TokenResponseDto.Builder builder() {
    return new OAuth2TokenResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OAuth2TokenResponseDto.Builder toBuilder() {
    OAuth2TokenResponseDto.Builder builder = new OAuth2TokenResponseDto.Builder();
    return builder.copyOf(this);
  }

}

