package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** A simple challenge platform. */
@Schema(name = "SimpleChallengePlatform", description = "A simple challenge platform.")
@JsonTypeName("SimpleChallengePlatform")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class SimpleChallengePlatformDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("name")
  private String name;

  public SimpleChallengePlatformDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge platform.
   *
   * @return id
   */
  @NotNull
  @Schema(
    name = "id",
    example = "1",
    description = "The unique identifier of a challenge platform.",
    required = true
  )
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
   *
   * @return slug
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 30)
  @Schema(
    name = "slug",
    example = "example-challenge-platform",
    description = "The slug of the challenge platform.",
    required = true
  )
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
   * The name of the challenge platform.
   *
   * @return name
   */
  @NotNull
  @Size(min = 3, max = 30)
  @Schema(name = "name", description = "The name of the challenge platform.", required = true)
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
    return (
      Objects.equals(this.id, simpleChallengePlatform.id) &&
      Objects.equals(this.slug, simpleChallengePlatform.slug) &&
      Objects.equals(this.name, simpleChallengePlatform.name)
    );
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
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
