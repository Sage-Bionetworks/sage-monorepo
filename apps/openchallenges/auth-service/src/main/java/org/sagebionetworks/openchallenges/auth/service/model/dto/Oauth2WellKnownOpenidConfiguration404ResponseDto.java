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
 * Oauth2WellKnownOpenidConfiguration404ResponseDto
 */

@JsonTypeName("oauth2WellKnownOpenidConfiguration_404_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2WellKnownOpenidConfiguration404ResponseDto {

  private @Nullable String error;

  private @Nullable String errorDescription;

  public Oauth2WellKnownOpenidConfiguration404ResponseDto error(@Nullable String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  
  @Schema(name = "error", example = "configuration_not_found", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public @Nullable String getError() {
    return error;
  }

  public void setError(@Nullable String error) {
    this.error = error;
  }

  public Oauth2WellKnownOpenidConfiguration404ResponseDto errorDescription(@Nullable String errorDescription) {
    this.errorDescription = errorDescription;
    return this;
  }

  /**
   * Get errorDescription
   * @return errorDescription
   */
  
  @Schema(name = "error_description", example = "OAuth2 configuration is not available", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error_description")
  public @Nullable String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(@Nullable String errorDescription) {
    this.errorDescription = errorDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2WellKnownOpenidConfiguration404ResponseDto oauth2WellKnownOpenidConfiguration404Response = (Oauth2WellKnownOpenidConfiguration404ResponseDto) o;
    return Objects.equals(this.error, oauth2WellKnownOpenidConfiguration404Response.error) &&
        Objects.equals(this.errorDescription, oauth2WellKnownOpenidConfiguration404Response.errorDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, errorDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2WellKnownOpenidConfiguration404ResponseDto {\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    errorDescription: ").append(toIndentedString(errorDescription)).append("\n");
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

    private Oauth2WellKnownOpenidConfiguration404ResponseDto instance;

    public Builder() {
      this(new Oauth2WellKnownOpenidConfiguration404ResponseDto());
    }

    protected Builder(Oauth2WellKnownOpenidConfiguration404ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2WellKnownOpenidConfiguration404ResponseDto value) { 
      this.instance.setError(value.error);
      this.instance.setErrorDescription(value.errorDescription);
      return this;
    }

    public Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder error(String error) {
      this.instance.error(error);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder errorDescription(String errorDescription) {
      this.instance.errorDescription(errorDescription);
      return this;
    }
    
    /**
    * returns a built Oauth2WellKnownOpenidConfiguration404ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2WellKnownOpenidConfiguration404ResponseDto build() {
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
  public static Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder builder() {
    return new Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder toBuilder() {
    Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder builder = new Oauth2WellKnownOpenidConfiguration404ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

