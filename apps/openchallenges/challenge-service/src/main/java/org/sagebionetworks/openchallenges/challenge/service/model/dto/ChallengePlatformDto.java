package org.sagebionetworks.openchallenges.challenge.service.model.dto;

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
 * A challenge platform
 */

@Schema(name = "ChallengePlatform", description = "A challenge platform")
@JsonTypeName("ChallengePlatform")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformDto {

  private Long id;

  private String slug;

  private String name;

  private String avatarKey;

  private String websiteUrl = null;

  public ChallengePlatformDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformDto(Long id, String slug, String name, String avatarKey, String websiteUrl) {
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.avatarKey = avatarKey;
    this.websiteUrl = websiteUrl;
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
   * The display name of the challenge platform.
   * @return name
   */
  @NotNull @Size(min = 3, max = 50) 
  @Schema(name = "name", example = "Example Challenge Platform", description = "The display name of the challenge platform.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengePlatformDto avatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
    return this;
  }

  /**
   * The avatar key
   * @return avatarKey
   */
  @NotNull 
  @Schema(name = "avatarKey", example = "logo/dream.png", description = "The avatar key", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("avatarKey")
  public String getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
  }

  public ChallengePlatformDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   * @return websiteUrl
   */
  @NotNull @Size(max = 500) 
  @Schema(name = "websiteUrl", example = "https://openchallenges.io", description = "A URL to the website or image.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("websiteUrl")
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
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
        Objects.equals(this.avatarKey, challengePlatform.avatarKey) &&
        Objects.equals(this.websiteUrl, challengePlatform.websiteUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, name, avatarKey, websiteUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    avatarKey: ").append(toIndentedString(avatarKey)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
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
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
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
    
    public ChallengePlatformDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public ChallengePlatformDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
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

