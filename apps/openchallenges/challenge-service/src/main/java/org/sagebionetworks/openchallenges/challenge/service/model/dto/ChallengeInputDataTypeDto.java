package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

/** A challenge EDAM annotation. */
@Schema(name = "ChallengeInputDataType", description = "A challenge EDAM annotation.")
@JsonTypeName("ChallengeInputDataType")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeInputDataTypeDto {

  @JsonProperty("edamId")
  private String edamId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("subclassOf")
  private String subclassOf = null;

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ChallengeInputDataTypeDto edamId(String edamId) {
    this.edamId = edamId;
    return this;
  }

  /**
   * The prefix Internationalized Resource Identifier (IRI) of the EDAM ontology term.
   *
   * @return edamId
   */
  @NotNull
  @Size(min = 3, max = 16)
  @Schema(
      name = "edamId",
      example = "data_1916",
      description =
          "The prefix Internationalized Resource Identifier (IRI) of the EDAM ontology term.",
      required = true)
  public String getEdamId() {
    return edamId;
  }

  public void setEdamId(String edamId) {
    this.edamId = edamId;
  }

  public ChallengeInputDataTypeDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The preferred name/label of the EDAM ontology term.
   *
   * @return name
   */
  @NotNull
  @Size(min = 3, max = 80)
  @Schema(
      name = "name",
      example = "Alignment",
      description = "The preferred name/label of the EDAM ontology term.",
      required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeInputDataTypeDto subclassOf(String subclassOf) {
    this.subclassOf = subclassOf;
    return this;
  }

  /**
   * The parent EDAM ID(s) of the EDAM ontology term.
   *
   * @return subclassOf
   */
  @Size(min = 3, max = 60)
  @Schema(
      name = "subclassOf",
      example = "data_0006",
      description = "The parent EDAM ID(s) of the EDAM ontology term.",
      required = false)
  public String getSubclassOf() {
    return subclassOf;
  }

  public void setSubclassOf(String subclassOf) {
    this.subclassOf = subclassOf;
  }

  public ChallengeInputDataTypeDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   *
   * @return createdAt
   */
  @NotNull
  @Valid
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", required = true)
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ChallengeInputDataTypeDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   *
   * @return updatedAt
   */
  @NotNull
  @Valid
  @Schema(name = "updatedAt", required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeInputDataTypeDto challengeInputDataType = (ChallengeInputDataTypeDto) o;
    return Objects.equals(this.edamId, challengeInputDataType.edamId)
        && Objects.equals(this.name, challengeInputDataType.name)
        && Objects.equals(this.subclassOf, challengeInputDataType.subclassOf)
        && Objects.equals(this.createdAt, challengeInputDataType.createdAt)
        && Objects.equals(this.updatedAt, challengeInputDataType.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edamId, name, subclassOf, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeInputDataTypeDto {\n");
    sb.append("    edamId: ").append(toIndentedString(edamId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    subclassOf: ").append(toIndentedString(subclassOf)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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
