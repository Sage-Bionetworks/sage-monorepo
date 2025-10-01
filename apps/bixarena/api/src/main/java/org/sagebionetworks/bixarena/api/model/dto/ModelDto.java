package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A model entity.
 */

@Schema(name = "Model", description = "A model entity.")
@JsonTypeName("Model")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelDto {

  private String id;

  private String slug;

  private String name;

  private @Nullable String license = null;

  private Boolean active;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ModelDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelDto(String id, String slug, String name, Boolean active, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.active = active;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public ModelDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier (UUID) of the model.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f", description = "Unique identifier (UUID) of the model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ModelDto slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * URL-friendly unique slug for the model.
   * @return slug
   */
  @NotNull 
  @Schema(name = "slug", example = "my-awesome-model", description = "URL-friendly unique slug for the model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("slug")
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ModelDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human-readable name of the model.
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "My Awesome Model", description = "Human-readable name of the model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ModelDto license(@Nullable String license) {
    this.license = license;
    return this;
  }

  /**
   * License under which the model is released.
   * @return license
   */
  
  @Schema(name = "license", example = "Apache-2.0", description = "License under which the model is released.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("license")
  public @Nullable String getLicense() {
    return license;
  }

  public void setLicense(@Nullable String license) {
    this.license = license;
  }

  public ModelDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Whether the model is active/visible.
   * @return active
   */
  @NotNull 
  @Schema(name = "active", example = "true", description = "Whether the model is active/visible.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public ModelDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When the model was created.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-09-15T12:00Z", description = "When the model was created.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ModelDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * When the model was last updated.
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2025-09-20T08:30Z", description = "When the model was last updated.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelDto model = (ModelDto) o;
    return Objects.equals(this.id, model.id) &&
        Objects.equals(this.slug, model.slug) &&
        Objects.equals(this.name, model.name) &&
        Objects.equals(this.license, model.license) &&
        Objects.equals(this.active, model.active) &&
        Objects.equals(this.createdAt, model.createdAt) &&
        Objects.equals(this.updatedAt, model.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, name, license, active, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

    private ModelDto instance;

    public Builder() {
      this(new ModelDto());
    }

    protected Builder(ModelDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelDto value) { 
      this.instance.setId(value.id);
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setLicense(value.license);
      this.instance.setActive(value.active);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public ModelDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ModelDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ModelDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ModelDto.Builder license(String license) {
      this.instance.license(license);
      return this;
    }
    
    public ModelDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public ModelDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public ModelDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built ModelDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelDto build() {
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
  public static ModelDto.Builder builder() {
    return new ModelDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelDto.Builder toBuilder() {
    ModelDto.Builder builder = new ModelDto.Builder();
    return builder.copyOf(this);
  }

}

