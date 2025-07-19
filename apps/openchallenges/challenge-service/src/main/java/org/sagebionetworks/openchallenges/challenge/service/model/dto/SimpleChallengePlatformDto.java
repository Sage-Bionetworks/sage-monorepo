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
 * A simple challenge platform.
 */

@Schema(name = "SimpleChallengePlatform", description = "A simple challenge platform.")
@JsonTypeName("SimpleChallengePlatform")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class SimpleChallengePlatformDto {

  private Long id;

  private String slug;

  private String name;

  public SimpleChallengePlatformDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SimpleChallengePlatformDto(Long id, String slug, String name) {
    this.id = id;
    this.slug = slug;
    this.name = name;
  }

  public SimpleChallengePlatformDto id(Long id) {
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

  public SimpleChallengePlatformDto slug(String slug) {
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

  public SimpleChallengePlatformDto name(String name) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleChallengePlatformDto simpleChallengePlatform = (SimpleChallengePlatformDto) o;
    return Objects.equals(this.id, simpleChallengePlatform.id) &&
        Objects.equals(this.slug, simpleChallengePlatform.slug) &&
        Objects.equals(this.name, simpleChallengePlatform.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimpleChallengePlatformDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

    private SimpleChallengePlatformDto instance;

    public Builder() {
      this(new SimpleChallengePlatformDto());
    }

    protected Builder(SimpleChallengePlatformDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SimpleChallengePlatformDto value) { 
      this.instance.setId(value.id);
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      return this;
    }

    public SimpleChallengePlatformDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public SimpleChallengePlatformDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public SimpleChallengePlatformDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    /**
    * returns a built SimpleChallengePlatformDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SimpleChallengePlatformDto build() {
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
  public static SimpleChallengePlatformDto.Builder builder() {
    return new SimpleChallengePlatformDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SimpleChallengePlatformDto.Builder toBuilder() {
    SimpleChallengePlatformDto.Builder builder = new SimpleChallengePlatformDto.Builder();
    return builder.copyOf(this);
  }

}

