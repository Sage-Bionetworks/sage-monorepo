package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** An EDAM Operation concept. */
@Schema(name = "EdamOperation", description = "An EDAM Operation concept.")
@JsonTypeName("EdamOperation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class EdamOperationDto {

  @JsonProperty("classId")
  private String classId;

  @JsonProperty("preferredLabel")
  private String preferredLabel;

  public EdamOperationDto classId(String classId) {
    this.classId = classId;
    return this;
  }

  /**
   * Get classId
   *
   * @return classId
   */
  @NotNull
  @Pattern(regexp = "^http://edamontology\\.org/operation_\\d+$")
  @Size(max = 60)
  @Schema(name = "classId", example = "http://edamontology.org/operation_0230", required = true)
  public String getClassId() {
    return classId;
  }

  public void setClassId(String classId) {
    this.classId = classId;
  }

  public EdamOperationDto preferredLabel(String preferredLabel) {
    this.preferredLabel = preferredLabel;
    return this;
  }

  /**
   * Get preferredLabel
   *
   * @return preferredLabel
   */
  @NotNull
  @Size(max = 80)
  @Schema(name = "preferredLabel", example = "Sequence generation", required = true)
  public String getPreferredLabel() {
    return preferredLabel;
  }

  public void setPreferredLabel(String preferredLabel) {
    this.preferredLabel = preferredLabel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EdamOperationDto edamOperation = (EdamOperationDto) o;
    return Objects.equals(this.classId, edamOperation.classId)
        && Objects.equals(this.preferredLabel, edamOperation.preferredLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classId, preferredLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdamOperationDto {\n");
    sb.append("    classId: ").append(toIndentedString(classId)).append("\n");
    sb.append("    preferredLabel: ").append(toIndentedString(preferredLabel)).append("\n");
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
