package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The information used to create a challenge platform */
@Schema(
    name = "challenge-platform-create-request",
    description = "The information used to create a challenge platform")
@JsonTypeName("challenge-platform-create-request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengePlatformCreateRequestDto {

  @JsonProperty("name")
  private String name;

  public ChallengePlatformCreateRequestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the challenge platform.
   *
   * @return name
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 30)
  @Schema(
      name = "name",
      example = "example-challenge-platform",
      description = "The name of the challenge platform.",
      required = true)
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
    ChallengePlatformCreateRequestDto challengePlatformCreateRequest =
        (ChallengePlatformCreateRequestDto) o;
    return Objects.equals(this.name, challengePlatformCreateRequest.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformCreateRequestDto {\n");
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
