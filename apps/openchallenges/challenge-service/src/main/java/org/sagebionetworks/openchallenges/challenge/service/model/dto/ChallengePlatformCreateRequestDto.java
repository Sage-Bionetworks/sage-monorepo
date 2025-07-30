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
 * The information used to create a challenge platform
 */

@Schema(name = "ChallengePlatformCreateRequest", description = "The information used to create a challenge platform")
@JsonTypeName("ChallengePlatformCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformCreateRequestDto {

  private String slug;

  private String name;

  private String avatarKey;

  private String websiteUrl = null;

  public ChallengePlatformCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformCreateRequestDto(String slug, String name, String avatarKey, String websiteUrl) {
    this.slug = slug;
    this.name = name;
    this.avatarKey = avatarKey;
    this.websiteUrl = websiteUrl;
  }

  public ChallengePlatformCreateRequestDto slug(String slug) {
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

  public ChallengePlatformCreateRequestDto name(String name) {
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

  public ChallengePlatformCreateRequestDto avatarKey(String avatarKey) {
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

  public ChallengePlatformCreateRequestDto websiteUrl(String websiteUrl) {
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
    ChallengePlatformCreateRequestDto challengePlatformCreateRequest = (ChallengePlatformCreateRequestDto) o;
    return Objects.equals(this.slug, challengePlatformCreateRequest.slug) &&
        Objects.equals(this.name, challengePlatformCreateRequest.name) &&
        Objects.equals(this.avatarKey, challengePlatformCreateRequest.avatarKey) &&
        Objects.equals(this.websiteUrl, challengePlatformCreateRequest.websiteUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slug, name, avatarKey, websiteUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformCreateRequestDto {\n");
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

    private ChallengePlatformCreateRequestDto instance;

    public Builder() {
      this(new ChallengePlatformCreateRequestDto());
    }

    protected Builder(ChallengePlatformCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformCreateRequestDto value) { 
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
      return this;
    }

    public ChallengePlatformCreateRequestDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ChallengePlatformCreateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengePlatformCreateRequestDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public ChallengePlatformCreateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformCreateRequestDto build() {
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
  public static ChallengePlatformCreateRequestDto.Builder builder() {
    return new ChallengePlatformCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformCreateRequestDto.Builder toBuilder() {
    ChallengePlatformCreateRequestDto.Builder builder = new ChallengePlatformCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

