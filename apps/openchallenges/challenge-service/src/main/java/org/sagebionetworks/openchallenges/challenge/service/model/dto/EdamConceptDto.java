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
 * The EDAM concept.
 */

@Schema(name = "EdamConcept", description = "The EDAM concept.")
@JsonTypeName("EdamConcept")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class EdamConceptDto {

  private Integer id;

  private String classId;

  private String preferredLabel;

  public EdamConceptDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EdamConceptDto(Integer id, String classId, String preferredLabel) {
    this.id = id;
    this.classId = classId;
    this.preferredLabel = preferredLabel;
  }

  public EdamConceptDto id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the EDAM concept.
   * minimum: 1
   * @return id
   */
  @NotNull @Min(1) 
  @Schema(name = "id", example = "1", description = "The unique identifier of the EDAM concept.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public EdamConceptDto classId(String classId) {
    this.classId = classId;
    return this;
  }

  /**
   * Get classId
   * @return classId
   */
  @NotNull @Size(max = 60) 
  @Schema(name = "classId", example = "http://edamontology.org/data_0850", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("classId")
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
   * @return preferredLabel
   */
  @NotNull @Size(max = 80) 
  @Schema(name = "preferredLabel", example = "Sequence set", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("preferredLabel")
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
    return Objects.equals(this.id, edamConcept.id) &&
        Objects.equals(this.classId, edamConcept.classId) &&
        Objects.equals(this.preferredLabel, edamConcept.preferredLabel);
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

    private EdamConceptDto instance;

    public Builder() {
      this(new EdamConceptDto());
    }

    protected Builder(EdamConceptDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EdamConceptDto value) { 
      this.instance.setId(value.id);
      this.instance.setClassId(value.classId);
      this.instance.setPreferredLabel(value.preferredLabel);
      return this;
    }

    public EdamConceptDto.Builder id(Integer id) {
      this.instance.id(id);
      return this;
    }
    
    public EdamConceptDto.Builder classId(String classId) {
      this.instance.classId(classId);
      return this;
    }
    
    public EdamConceptDto.Builder preferredLabel(String preferredLabel) {
      this.instance.preferredLabel(preferredLabel);
      return this;
    }
    
    /**
    * returns a built EdamConceptDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EdamConceptDto build() {
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
  public static EdamConceptDto.Builder builder() {
    return new EdamConceptDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EdamConceptDto.Builder toBuilder() {
    EdamConceptDto.Builder builder = new EdamConceptDto.Builder();
    return builder.copyOf(this);
  }

}

