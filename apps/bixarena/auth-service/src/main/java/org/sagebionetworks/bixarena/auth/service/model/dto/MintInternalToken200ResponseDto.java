package org.sagebionetworks.bixarena.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MintInternalToken200ResponseDto
 */

@JsonTypeName("mintInternalToken_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MintInternalToken200ResponseDto {

  private String accessToken;

  /**
   * Gets or Sets tokenType
   */
  public enum TokenTypeEnum {
    BEARER("Bearer");

    private final String value;

    TokenTypeEnum(String value) {
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
    public static TokenTypeEnum fromValue(String value) {
      for (TokenTypeEnum b : TokenTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TokenTypeEnum tokenType;

  private Integer expiresIn;

  public MintInternalToken200ResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MintInternalToken200ResponseDto(String accessToken, TokenTypeEnum tokenType, Integer expiresIn) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
  }

  public MintInternalToken200ResponseDto accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * Get accessToken
   * @return accessToken
   */
  @NotNull 
  @Schema(name = "access_token", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("access_token")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public MintInternalToken200ResponseDto tokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  /**
   * Get tokenType
   * @return tokenType
   */
  @NotNull 
  @Schema(name = "token_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token_type")
  public TokenTypeEnum getTokenType() {
    return tokenType;
  }

  public void setTokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
  }

  public MintInternalToken200ResponseDto expiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Get expiresIn
   * @return expiresIn
   */
  @NotNull 
  @Schema(name = "expires_in", example = "600", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expires_in")
  public Integer getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Integer expiresIn) {
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
    MintInternalToken200ResponseDto mintInternalToken200Response = (MintInternalToken200ResponseDto) o;
    return Objects.equals(this.accessToken, mintInternalToken200Response.accessToken) &&
        Objects.equals(this.tokenType, mintInternalToken200Response.tokenType) &&
        Objects.equals(this.expiresIn, mintInternalToken200Response.expiresIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, tokenType, expiresIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MintInternalToken200ResponseDto {\n");
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

    private MintInternalToken200ResponseDto instance;

    public Builder() {
      this(new MintInternalToken200ResponseDto());
    }

    protected Builder(MintInternalToken200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MintInternalToken200ResponseDto value) { 
      this.instance.setAccessToken(value.accessToken);
      this.instance.setTokenType(value.tokenType);
      this.instance.setExpiresIn(value.expiresIn);
      return this;
    }

    public MintInternalToken200ResponseDto.Builder accessToken(String accessToken) {
      this.instance.accessToken(accessToken);
      return this;
    }
    
    public MintInternalToken200ResponseDto.Builder tokenType(TokenTypeEnum tokenType) {
      this.instance.tokenType(tokenType);
      return this;
    }
    
    public MintInternalToken200ResponseDto.Builder expiresIn(Integer expiresIn) {
      this.instance.expiresIn(expiresIn);
      return this;
    }
    
    /**
    * returns a built MintInternalToken200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MintInternalToken200ResponseDto build() {
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
  public static MintInternalToken200ResponseDto.Builder builder() {
    return new MintInternalToken200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MintInternalToken200ResponseDto.Builder toBuilder() {
    MintInternalToken200ResponseDto.Builder builder = new MintInternalToken200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

