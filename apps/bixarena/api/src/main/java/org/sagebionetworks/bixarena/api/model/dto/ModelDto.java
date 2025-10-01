package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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

  private @Nullable String alias = null;

  private String name;

  private @Nullable String organization = null;

  /**
   * Whether the model is open-source or commercial.
   */
  public enum LicenseEnum {
    OPEN_SOURCE("open-source"),
    
    COMMERCIAL("commercial");

    private final String value;

    LicenseEnum(String value) {
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
    public static LicenseEnum fromValue(String value) {
      for (LicenseEnum b : LicenseEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private LicenseEnum license;

  private Boolean active;

  private String externalLink;

  private @Nullable String description = null;

  private String apiModelName;

  private String apiBase;

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
  public ModelDto(String id, String slug, String name, LicenseEnum license, Boolean active, String externalLink, String apiModelName, String apiBase, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.license = license;
    this.active = active;
    this.externalLink = externalLink;
    this.apiModelName = apiModelName;
    this.apiBase = apiBase;
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

  public ModelDto alias(@Nullable String alias) {
    this.alias = alias;
    return this;
  }

  /**
   * Alternative name or alias for the model.
   * @return alias
   */
  
  @Schema(name = "alias", example = "awesome-model-v2", description = "Alternative name or alias for the model.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alias")
  public @Nullable String getAlias() {
    return alias;
  }

  public void setAlias(@Nullable String alias) {
    this.alias = alias;
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

  public ModelDto organization(@Nullable String organization) {
    this.organization = organization;
    return this;
  }

  /**
   * Organization that developed or maintains the model.
   * @return organization
   */
  
  @Schema(name = "organization", example = "OpenAI", description = "Organization that developed or maintains the model.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("organization")
  public @Nullable String getOrganization() {
    return organization;
  }

  public void setOrganization(@Nullable String organization) {
    this.organization = organization;
  }

  public ModelDto license(LicenseEnum license) {
    this.license = license;
    return this;
  }

  /**
   * Whether the model is open-source or commercial.
   * @return license
   */
  @NotNull 
  @Schema(name = "license", example = "open-source", description = "Whether the model is open-source or commercial.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("license")
  public LicenseEnum getLicense() {
    return license;
  }

  public void setLicense(LicenseEnum license) {
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

  public ModelDto externalLink(String externalLink) {
    this.externalLink = externalLink;
    return this;
  }

  /**
   * External URL with more information about the model.
   * @return externalLink
   */
  @NotNull 
  @Schema(name = "externalLink", example = "https://openrouter.ai/models/openai/gpt-4", description = "External URL with more information about the model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("externalLink")
  public String getExternalLink() {
    return externalLink;
  }

  public void setExternalLink(String externalLink) {
    this.externalLink = externalLink;
  }

  public ModelDto description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description of the model.
   * @return description
   */
  
  @Schema(name = "description", example = "A large multimodal model that can process text and images.", description = "Detailed description of the model.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public ModelDto apiModelName(String apiModelName) {
    this.apiModelName = apiModelName;
    return this;
  }

  /**
   * The model name used for API calls.
   * @return apiModelName
   */
  @NotNull 
  @Schema(name = "apiModelName", example = "anthropic/claude-sonnet-4.5", description = "The model name used for API calls.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("apiModelName")
  public String getApiModelName() {
    return apiModelName;
  }

  public void setApiModelName(String apiModelName) {
    this.apiModelName = apiModelName;
  }

  public ModelDto apiBase(String apiBase) {
    this.apiBase = apiBase;
    return this;
  }

  /**
   * Base URL for the model API.
   * @return apiBase
   */
  @NotNull 
  @Schema(name = "apiBase", example = "https://openrouter.ai/api/v1", description = "Base URL for the model API.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("apiBase")
  public String getApiBase() {
    return apiBase;
  }

  public void setApiBase(String apiBase) {
    this.apiBase = apiBase;
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
        Objects.equals(this.alias, model.alias) &&
        Objects.equals(this.name, model.name) &&
        Objects.equals(this.organization, model.organization) &&
        Objects.equals(this.license, model.license) &&
        Objects.equals(this.active, model.active) &&
        Objects.equals(this.externalLink, model.externalLink) &&
        Objects.equals(this.description, model.description) &&
        Objects.equals(this.apiModelName, model.apiModelName) &&
        Objects.equals(this.apiBase, model.apiBase) &&
        Objects.equals(this.createdAt, model.createdAt) &&
        Objects.equals(this.updatedAt, model.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, alias, name, organization, license, active, externalLink, description, apiModelName, apiBase, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    externalLink: ").append(toIndentedString(externalLink)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    apiModelName: ").append(toIndentedString(apiModelName)).append("\n");
    sb.append("    apiBase: ").append(toIndentedString(apiBase)).append("\n");
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
      this.instance.setAlias(value.alias);
      this.instance.setName(value.name);
      this.instance.setOrganization(value.organization);
      this.instance.setLicense(value.license);
      this.instance.setActive(value.active);
      this.instance.setExternalLink(value.externalLink);
      this.instance.setDescription(value.description);
      this.instance.setApiModelName(value.apiModelName);
      this.instance.setApiBase(value.apiBase);
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
    
    public ModelDto.Builder alias(String alias) {
      this.instance.alias(alias);
      return this;
    }
    
    public ModelDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ModelDto.Builder organization(String organization) {
      this.instance.organization(organization);
      return this;
    }
    
    public ModelDto.Builder license(LicenseEnum license) {
      this.instance.license(license);
      return this;
    }
    
    public ModelDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public ModelDto.Builder externalLink(String externalLink) {
      this.instance.externalLink(externalLink);
      return this;
    }
    
    public ModelDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public ModelDto.Builder apiModelName(String apiModelName) {
      this.instance.apiModelName(apiModelName);
      return this;
    }
    
    public ModelDto.Builder apiBase(String apiBase) {
      this.instance.apiBase(apiBase);
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

