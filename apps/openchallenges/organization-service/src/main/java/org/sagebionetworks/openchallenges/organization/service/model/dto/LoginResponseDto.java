package org.sagebionetworks.openchallenges.organization.service.model.dto;

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
 * LoginResponseDto
 */

@JsonTypeName("LoginResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class LoginResponseDto {

  private @Nullable String token;

  private @Nullable Integer expiresIn;

  private @Nullable String tokenType;

  public LoginResponseDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * JWT access token
   * @return token
   */
  
  @Schema(name = "token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT access token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public LoginResponseDto expiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Token expiration time in seconds
   * @return expiresIn
   */
  
  @Schema(name = "expiresIn", example = "86400", description = "Token expiration time in seconds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresIn")
  public Integer getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
  }

  public LoginResponseDto tokenType(String tokenType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginResponseDto loginResponse = (LoginResponseDto) o;
    return Objects.equals(this.token, loginResponse.token) &&
        Objects.equals(this.expiresIn, loginResponse.expiresIn) &&
        Objects.equals(this.tokenType, loginResponse.tokenType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, expiresIn, tokenType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginResponseDto {\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
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
      this.instance.setToken(value.token);
      this.instance.setExpiresIn(value.expiresIn);
      this.instance.setTokenType(value.tokenType);
      return this;
    }

    public LoginResponseDto.Builder token(String token) {
      this.instance.token(token);
      return this;
    }
    
    public LoginResponseDto.Builder expiresIn(Integer expiresIn) {
      this.instance.expiresIn(expiresIn);
      return this;
    }
    
    public LoginResponseDto.Builder tokenType(String tokenType) {
      this.instance.tokenType(tokenType);
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

