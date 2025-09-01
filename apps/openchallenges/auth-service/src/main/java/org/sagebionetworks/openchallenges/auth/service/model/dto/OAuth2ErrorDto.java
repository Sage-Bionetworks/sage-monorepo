package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OAuth2 error response
 */

@Schema(name = "OAuth2Error", description = "OAuth2 error response")
@JsonTypeName("OAuth2Error")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OAuth2ErrorDto {

  /**
   * Error code
   */
  public enum ErrorEnum {
    INVALID_REQUEST("invalid_request"),
    
    INVALID_CLIENT("invalid_client"),
    
    INVALID_GRANT("invalid_grant"),
    
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),
    
    INVALID_SCOPE("invalid_scope");

    private final String value;

    ErrorEnum(String value) {
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
    public static ErrorEnum fromValue(String value) {
      for (ErrorEnum b : ErrorEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ErrorEnum error;

  private @Nullable String errorDescription;

  private @Nullable URI errorUri;

  public OAuth2ErrorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OAuth2ErrorDto(ErrorEnum error) {
    this.error = error;
  }

  public OAuth2ErrorDto error(ErrorEnum error) {
    this.error = error;
    return this;
  }

  /**
   * Error code
   * @return error
   */
  @NotNull 
  @Schema(name = "error", example = "invalid_request", description = "Error code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("error")
  public ErrorEnum getError() {
    return error;
  }

  public void setError(ErrorEnum error) {
    this.error = error;
  }

  public OAuth2ErrorDto errorDescription(@Nullable String errorDescription) {
    this.errorDescription = errorDescription;
    return this;
  }

  /**
   * Human-readable error description
   * @return errorDescription
   */
  
  @Schema(name = "error_description", example = "The request is missing a required parameter", description = "Human-readable error description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error_description")
  public @Nullable String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(@Nullable String errorDescription) {
    this.errorDescription = errorDescription;
  }

  public OAuth2ErrorDto errorUri(@Nullable URI errorUri) {
    this.errorUri = errorUri;
    return this;
  }

  /**
   * URI to documentation about the error
   * @return errorUri
   */
  @Valid 
  @Schema(name = "error_uri", description = "URI to documentation about the error", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error_uri")
  public @Nullable URI getErrorUri() {
    return errorUri;
  }

  public void setErrorUri(@Nullable URI errorUri) {
    this.errorUri = errorUri;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OAuth2ErrorDto oauth2Error = (OAuth2ErrorDto) o;
    return Objects.equals(this.error, oauth2Error.error) &&
        Objects.equals(this.errorDescription, oauth2Error.errorDescription) &&
        Objects.equals(this.errorUri, oauth2Error.errorUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, errorDescription, errorUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OAuth2ErrorDto {\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    errorDescription: ").append(toIndentedString(errorDescription)).append("\n");
    sb.append("    errorUri: ").append(toIndentedString(errorUri)).append("\n");
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

    private OAuth2ErrorDto instance;

    public Builder() {
      this(new OAuth2ErrorDto());
    }

    protected Builder(OAuth2ErrorDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OAuth2ErrorDto value) { 
      this.instance.setError(value.error);
      this.instance.setErrorDescription(value.errorDescription);
      this.instance.setErrorUri(value.errorUri);
      return this;
    }

    public OAuth2ErrorDto.Builder error(ErrorEnum error) {
      this.instance.error(error);
      return this;
    }
    
    public OAuth2ErrorDto.Builder errorDescription(String errorDescription) {
      this.instance.errorDescription(errorDescription);
      return this;
    }
    
    public OAuth2ErrorDto.Builder errorUri(URI errorUri) {
      this.instance.errorUri(errorUri);
      return this;
    }
    
    /**
    * returns a built OAuth2ErrorDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OAuth2ErrorDto build() {
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
  public static OAuth2ErrorDto.Builder builder() {
    return new OAuth2ErrorDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OAuth2ErrorDto.Builder toBuilder() {
    OAuth2ErrorDto.Builder builder = new OAuth2ErrorDto.Builder();
    return builder.copyOf(this);
  }

}

