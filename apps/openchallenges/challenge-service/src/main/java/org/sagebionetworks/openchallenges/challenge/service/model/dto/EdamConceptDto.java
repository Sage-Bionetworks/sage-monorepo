package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The EDAM concept. */
@Schema(name = "EdamConcept", description = "The EDAM concept.")
@JsonTypeName("EdamConcept")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class EdamConceptDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("classId")
  private String classId;

  @JsonProperty("preferredLabel")
  private String preferredLabel;

  public EdamConceptDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the EDAM concept.
   *
   * @return id
   */
  @NotNull
  @Schema(
    name = "id",
    example = "1",
    description = "The unique identifier of the EDAM concept.",
    required = true
  )
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public EdamConceptDto classId(String classId) {
    this.classId = classId;
    return this;
  }

  /**
   * Get classId
   *
   * @return classId
   */
  @NotNull
  @Size(max = 60)
  @Schema(name = "classId", example = "http://edamontology.org/data_0850", required = true)
  public String getClassId() {
    return classId;
  }

  public void setClassId(String classId) {
    this.classId = classId;
  }

  public EdamConceptDto preferredLabel(String preferredLabel) {
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
  @Schema(name = "preferredLabel", example = "Sequence set", required = true)
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
    EdamConceptDto edamConcept = (EdamConceptDto) o;
    return (
      Objects.equals(this.id, edamConcept.id) &&
      Objects.equals(this.classId, edamConcept.classId) &&
      Objects.equals(this.preferredLabel, edamConcept.preferredLabel)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, classId, preferredLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdamConceptDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
