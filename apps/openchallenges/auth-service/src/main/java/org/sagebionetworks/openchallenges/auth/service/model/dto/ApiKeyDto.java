package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ApiKeyDto
 */

@JsonTypeName("ApiKey")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ApiKeyDto {

  private @Nullable UUID id;

  private @Nullable String name;

  private @Nullable String prefix;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime lastUsedAt = null;

  public ApiKeyDto id(@Nullable UUID id) {
    this.id = id;
    return this;
  }

  /**
   * API key ID
   * @return id
   */
  @Valid 
  @Schema(name = "id", example = "123e4567-e89b-12d3-a456-426614174000", description = "API key ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable UUID getId() {
    return id;
  }

  public void setId(@Nullable UUID id) {
    this.id = id;
  }

  public ApiKeyDto name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Human-readable name for the API key
   * @return name
   */
  
  @Schema(name = "name", example = "Production API Key", description = "Human-readable name for the API key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public ApiKeyDto prefix(@Nullable String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * First 8 characters of the API key for identification
   * @return prefix
   */
  
  @Schema(name = "prefix", example = "oc_prod_", description = "First 8 characters of the API key for identification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prefix")
  public @Nullable String getPrefix() {
    return prefix;
  }

  public void setPrefix(@Nullable String prefix) {
    this.prefix = prefix;
  }

  public ApiKeyDto createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When the API key was created
   * @return createdAt
   */
  @Valid 
  @Schema(name = "createdAt", example = "2024-01-15T10:30Z", description = "When the API key was created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdAt")
  public @Nullable OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ApiKeyDto expiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  /**
   * When the API key expires (null if no expiration)
   * @return expiresAt
   */
  @Valid 
  @Schema(name = "expiresAt", example = "2025-01-15T10:30Z", description = "When the API key expires (null if no expiration)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresAt")
  public @Nullable OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public ApiKeyDto lastUsedAt(@Nullable OffsetDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
    return this;
  }

  /**
   * When the API key was last used (null if never used)
   * @return lastUsedAt
   */
  @Valid 
  @Schema(name = "lastUsedAt", example = "2024-06-15T14:20Z", description = "When the API key was last used (null if never used)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastUsedAt")
  public @Nullable OffsetDateTime getLastUsedAt() {
    return lastUsedAt;
  }

  public void setLastUsedAt(@Nullable OffsetDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiKeyDto apiKey = (ApiKeyDto) o;
    return Objects.equals(this.id, apiKey.id) &&
        Objects.equals(this.name, apiKey.name) &&
        Objects.equals(this.prefix, apiKey.prefix) &&
        Objects.equals(this.createdAt, apiKey.createdAt) &&
        Objects.equals(this.expiresAt, apiKey.expiresAt) &&
        Objects.equals(this.lastUsedAt, apiKey.lastUsedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, prefix, createdAt, expiresAt, lastUsedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKeyDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
    sb.append("    lastUsedAt: ").append(toIndentedString(lastUsedAt)).append("\n");
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

    private ApiKeyDto instance;

    public Builder() {
      this(new ApiKeyDto());
    }

    protected Builder(ApiKeyDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ApiKeyDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setPrefix(value.prefix);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setExpiresAt(value.expiresAt);
      this.instance.setLastUsedAt(value.lastUsedAt);
      return this;
    }

    public ApiKeyDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public ApiKeyDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ApiKeyDto.Builder prefix(String prefix) {
      this.instance.prefix(prefix);
      return this;
    }
    
    public ApiKeyDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public ApiKeyDto.Builder expiresAt(OffsetDateTime expiresAt) {
      this.instance.expiresAt(expiresAt);
      return this;
    }
    
    public ApiKeyDto.Builder lastUsedAt(OffsetDateTime lastUsedAt) {
      this.instance.lastUsedAt(lastUsedAt);
      return this;
    }
    
    /**
    * returns a built ApiKeyDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ApiKeyDto build() {
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
  public static ApiKeyDto.Builder builder() {
    return new ApiKeyDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ApiKeyDto.Builder toBuilder() {
    ApiKeyDto.Builder builder = new ApiKeyDto.Builder();
    return builder.copyOf(this);
  }

}

