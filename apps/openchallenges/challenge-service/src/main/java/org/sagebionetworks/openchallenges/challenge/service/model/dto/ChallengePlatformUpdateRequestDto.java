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
 * A challenge platform update request.
 */

@Schema(name = "ChallengePlatformUpdateRequest", description = "A challenge platform update request.")
@JsonTypeName("ChallengePlatformUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformUpdateRequestDto {

  private String slug;

  private String name;

  private String avatarKey;

  private String websiteUrl = null;

  public ChallengePlatformUpdateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformUpdateRequestDto(String slug, String name, String avatarKey, String websiteUrl) {
    this.slug = slug;
    this.name = name;
    this.avatarKey = avatarKey;
    this.websiteUrl = websiteUrl;
  }

  public ChallengePlatformUpdateRequestDto slug(String slug) {
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

  public ChallengePlatformUpdateRequestDto name(String name) {
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

  public ChallengePlatformUpdateRequestDto avatarKey(String avatarKey) {
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

  public ChallengePlatformUpdateRequestDto websiteUrl(String websiteUrl) {
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
    ChallengePlatformUpdateRequestDto challengePlatformUpdateRequest = (ChallengePlatformUpdateRequestDto) o;
    return Objects.equals(this.slug, challengePlatformUpdateRequest.slug) &&
        Objects.equals(this.name, challengePlatformUpdateRequest.name) &&
        Objects.equals(this.avatarKey, challengePlatformUpdateRequest.avatarKey) &&
        Objects.equals(this.websiteUrl, challengePlatformUpdateRequest.websiteUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slug, name, avatarKey, websiteUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformUpdateRequestDto {\n");
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

    private ChallengePlatformUpdateRequestDto instance;

    public Builder() {
      this(new ChallengePlatformUpdateRequestDto());
    }

    protected Builder(ChallengePlatformUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformUpdateRequestDto value) { 
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
      return this;
    }

    public ChallengePlatformUpdateRequestDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ChallengePlatformUpdateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengePlatformUpdateRequestDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public ChallengePlatformUpdateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformUpdateRequestDto build() {
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
  public static ChallengePlatformUpdateRequestDto.Builder builder() {
    return new ChallengePlatformUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformUpdateRequestDto.Builder toBuilder() {
    ChallengePlatformUpdateRequestDto.Builder builder = new ChallengePlatformUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

