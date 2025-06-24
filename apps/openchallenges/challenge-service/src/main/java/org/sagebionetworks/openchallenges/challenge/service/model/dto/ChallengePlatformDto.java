package org.sagebionetworks.openchallenges.challenge.service.model.dto;

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
 * A challenge platform
 */

@Schema(name = "ChallengePlatform", description = "A challenge platform")
@JsonTypeName("ChallengePlatform")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformDto {

  private Long id;

  private String slug;

  private String name;

  private String avatarUrl;

  private String websiteUrl;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ChallengePlatformDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformDto(Long id, String slug, String name, String avatarUrl, String websiteUrl, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.avatarUrl = avatarUrl;
    this.websiteUrl = websiteUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public ChallengePlatformDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge platform.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of a challenge platform.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengePlatformDto slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * The slug of the challenge platform.
   * @return slug
   */
  @NotNull @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(min = 3, max = 30) 
  @Schema(name = "slug", example = "example-challenge-platform", description = "The slug of the challenge platform.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("slug")
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ChallengePlatformDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the challenge platform.
   * @return name
   */
  @NotNull @Size(min = 3, max = 30) 
  @Schema(name = "name", description = "The name of the challenge platform.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengePlatformDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * Get avatarUrl
   * @return avatarUrl
   */
  @NotNull 
  @Schema(name = "avatarUrl", example = "https://via.placeholder.com/300.png", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("avatarUrl")
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public ChallengePlatformDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * Get websiteUrl
   * @return websiteUrl
   */
  @NotNull 
  @Schema(name = "websiteUrl", example = "https://example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("websiteUrl")
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public ChallengePlatformDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ChallengePlatformDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ChallengePlatformDto challengePlatform = (ChallengePlatformDto) o;
    return Objects.equals(this.id, challengePlatform.id) &&
        Objects.equals(this.slug, challengePlatform.slug) &&
        Objects.equals(this.name, challengePlatform.name) &&
        Objects.equals(this.avatarUrl, challengePlatform.avatarUrl) &&
        Objects.equals(this.websiteUrl, challengePlatform.websiteUrl) &&
        Objects.equals(this.createdAt, challengePlatform.createdAt) &&
        Objects.equals(this.updatedAt, challengePlatform.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, name, avatarUrl, websiteUrl, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
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

    private ChallengePlatformDto instance;

    public Builder() {
      this(new ChallengePlatformDto());
    }

    protected Builder(ChallengePlatformDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformDto value) { 
      this.instance.setId(value.id);
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public ChallengePlatformDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public ChallengePlatformDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ChallengePlatformDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengePlatformDto.Builder avatarUrl(String avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public ChallengePlatformDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public ChallengePlatformDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public ChallengePlatformDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformDto build() {
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
  public static ChallengePlatformDto.Builder builder() {
    return new ChallengePlatformDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformDto.Builder toBuilder() {
    ChallengePlatformDto.Builder builder = new ChallengePlatformDto.Builder();
    return builder.copyOf(this);
  }

}

