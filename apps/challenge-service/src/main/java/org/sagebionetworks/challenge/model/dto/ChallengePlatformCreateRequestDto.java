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
    name = "ChallengePlatformCreateRequest",
    description = "The information used to create a challenge platform")
@JsonTypeName("ChallengePlatformCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengePlatformCreateRequestDto {

  @JsonProperty("displayName")
  private String displayName;

  public ChallengePlatformCreateRequestDto displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * The display name of the challenge platform.
   *
   * @return displayName
   */
  @NotNull
  @Size(min = 3, max = 50)
  @Schema(
      name = "displayName",
      example = "Example Challenge Platform",
      description = "The display name of the challenge platform.",
      required = true)
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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
    return Objects.equals(this.displayName, challengePlatformCreateRequest.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformCreateRequestDto {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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
