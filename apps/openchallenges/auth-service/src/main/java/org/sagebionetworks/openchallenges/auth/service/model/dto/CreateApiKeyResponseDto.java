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
 * CreateApiKeyResponseDto
 */

@JsonTypeName("CreateApiKeyResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CreateApiKeyResponseDto {

  private @Nullable UUID id;

  private @Nullable String key;

  private @Nullable String name;

  private @Nullable String prefix;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt = null;

  public CreateApiKeyResponseDto id(@Nullable UUID id) {
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

  public CreateApiKeyResponseDto key(@Nullable String key) {
    this.key = key;
    return this;
  }

  /**
   * The actual API key (only returned on creation)
   * @return key
   */
  
  @Schema(name = "key", example = "oc_prod_abcd1234567890abcdef1234567890abcdef1234", description = "The actual API key (only returned on creation)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("key")
  public @Nullable String getKey() {
    return key;
  }

  public void setKey(@Nullable String key) {
    this.key = key;
  }

  public CreateApiKeyResponseDto name(@Nullable String name) {
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

  public CreateApiKeyResponseDto prefix(@Nullable String prefix) {
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

  public CreateApiKeyResponseDto createdAt(@Nullable OffsetDateTime createdAt) {
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

  public CreateApiKeyResponseDto expiresAt(@Nullable OffsetDateTime expiresAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateApiKeyResponseDto createApiKeyResponse = (CreateApiKeyResponseDto) o;
    return Objects.equals(this.id, createApiKeyResponse.id) &&
        Objects.equals(this.key, createApiKeyResponse.key) &&
        Objects.equals(this.name, createApiKeyResponse.name) &&
        Objects.equals(this.prefix, createApiKeyResponse.prefix) &&
        Objects.equals(this.createdAt, createApiKeyResponse.createdAt) &&
        Objects.equals(this.expiresAt, createApiKeyResponse.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, key, name, prefix, createdAt, expiresAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateApiKeyResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
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

    private CreateApiKeyResponseDto instance;

    public Builder() {
      this(new CreateApiKeyResponseDto());
    }

    protected Builder(CreateApiKeyResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(CreateApiKeyResponseDto value) { 
      this.instance.setId(value.id);
      this.instance.setKey(value.key);
      this.instance.setName(value.name);
      this.instance.setPrefix(value.prefix);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setExpiresAt(value.expiresAt);
      return this;
    }

    public CreateApiKeyResponseDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public CreateApiKeyResponseDto.Builder key(String key) {
      this.instance.key(key);
      return this;
    }
    
    public CreateApiKeyResponseDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public CreateApiKeyResponseDto.Builder prefix(String prefix) {
      this.instance.prefix(prefix);
      return this;
    }
    
    public CreateApiKeyResponseDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public CreateApiKeyResponseDto.Builder expiresAt(OffsetDateTime expiresAt) {
      this.instance.expiresAt(expiresAt);
      return this;
    }
    
    /**
    * returns a built CreateApiKeyResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public CreateApiKeyResponseDto build() {
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
  public static CreateApiKeyResponseDto.Builder builder() {
    return new CreateApiKeyResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public CreateApiKeyResponseDto.Builder toBuilder() {
    CreateApiKeyResponseDto.Builder builder = new CreateApiKeyResponseDto.Builder();
    return builder.copyOf(this);
  }

}

