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
 * CreateApiKeyRequestDto
 */

@JsonTypeName("CreateApiKeyRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CreateApiKeyRequestDto {

  private String name;

  private @Nullable Integer expiresIn;

  public CreateApiKeyRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateApiKeyRequestDto(String name) {
    this.name = name;
  }

  public CreateApiKeyRequestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human-readable name for the API key
   * @return name
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "name", example = "Production API Key", description = "Human-readable name for the API key", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateApiKeyRequestDto expiresIn(@Nullable Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Number of days until the API key expires (optional, default is no expiration)
   * minimum: 1
   * maximum: 3650
   * @return expiresIn
   */
  @Min(1) @Max(3650) 
  @Schema(name = "expiresIn", example = "365", description = "Number of days until the API key expires (optional, default is no expiration)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    CreateApiKeyRequestDto createApiKeyRequest = (CreateApiKeyRequestDto) o;
    return Objects.equals(this.name, createApiKeyRequest.name) &&
        Objects.equals(this.expiresIn, createApiKeyRequest.expiresIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, expiresIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateApiKeyRequestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

    private CreateApiKeyRequestDto instance;

    public Builder() {
      this(new CreateApiKeyRequestDto());
    }

    protected Builder(CreateApiKeyRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(CreateApiKeyRequestDto value) { 
      this.instance.setName(value.name);
      this.instance.setExpiresIn(value.expiresIn);
      return this;
    }

    public CreateApiKeyRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public CreateApiKeyRequestDto.Builder expiresIn(Integer expiresIn) {
      this.instance.expiresIn(expiresIn);
      return this;
    }
    
    /**
    * returns a built CreateApiKeyRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public CreateApiKeyRequestDto build() {
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
  public static CreateApiKeyRequestDto.Builder builder() {
    return new CreateApiKeyRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public CreateApiKeyRequestDto.Builder toBuilder() {
    CreateApiKeyRequestDto.Builder builder = new CreateApiKeyRequestDto.Builder();
    return builder.copyOf(this);
  }

}

