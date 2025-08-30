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
 * RefreshTokenRequestDto
 */

@JsonTypeName("RefreshTokenRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class RefreshTokenRequestDto {

  private String refreshToken;

  public RefreshTokenRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RefreshTokenRequestDto(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public RefreshTokenRequestDto refreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
    return this;
  }

  /**
   * JWT refresh token
   * @return refreshToken
   */
  @NotNull 
  @Schema(name = "refreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("refreshToken")
  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefreshTokenRequestDto refreshTokenRequest = (RefreshTokenRequestDto) o;
    return Objects.equals(this.refreshToken, refreshTokenRequest.refreshToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(refreshToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefreshTokenRequestDto {\n");
    sb.append("    refreshToken: ").append(toIndentedString(refreshToken)).append("\n");
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

    private RefreshTokenRequestDto instance;

    public Builder() {
      this(new RefreshTokenRequestDto());
    }

    protected Builder(RefreshTokenRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RefreshTokenRequestDto value) { 
      this.instance.setRefreshToken(value.refreshToken);
      return this;
    }

    public RefreshTokenRequestDto.Builder refreshToken(String refreshToken) {
      this.instance.refreshToken(refreshToken);
      return this;
    }
    
    /**
    * returns a built RefreshTokenRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RefreshTokenRequestDto build() {
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
  public static RefreshTokenRequestDto.Builder builder() {
    return new RefreshTokenRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RefreshTokenRequestDto.Builder toBuilder() {
    RefreshTokenRequestDto.Builder builder = new RefreshTokenRequestDto.Builder();
    return builder.copyOf(this);
  }

}

