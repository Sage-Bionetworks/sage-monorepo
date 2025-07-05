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
 * ValidateApiKeyRequestDto
 */

@JsonTypeName("ValidateApiKeyRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ValidateApiKeyRequestDto {

  private String apiKey;

  public ValidateApiKeyRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValidateApiKeyRequestDto(String apiKey) {
    this.apiKey = apiKey;
  }

  public ValidateApiKeyRequestDto apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  /**
   * The API key to validate
   * @return apiKey
   */
  @NotNull 
  @Schema(name = "apiKey", example = "oc_prod_abcd1234567890abcdef1234567890abcdef1234", description = "The API key to validate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidateApiKeyRequestDto validateApiKeyRequest = (ValidateApiKeyRequestDto) o;
    return Objects.equals(this.apiKey, validateApiKeyRequest.apiKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidateApiKeyRequestDto {\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
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

    private ValidateApiKeyRequestDto instance;

    public Builder() {
      this(new ValidateApiKeyRequestDto());
    }

    protected Builder(ValidateApiKeyRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ValidateApiKeyRequestDto value) { 
      this.instance.setApiKey(value.apiKey);
      return this;
    }

    public ValidateApiKeyRequestDto.Builder apiKey(String apiKey) {
      this.instance.apiKey(apiKey);
      return this;
    }
    
    /**
    * returns a built ValidateApiKeyRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ValidateApiKeyRequestDto build() {
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
  public static ValidateApiKeyRequestDto.Builder builder() {
    return new ValidateApiKeyRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ValidateApiKeyRequestDto.Builder toBuilder() {
    ValidateApiKeyRequestDto.Builder builder = new ValidateApiKeyRequestDto.Builder();
    return builder.copyOf(this);
  }

}

