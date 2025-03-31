package org.sagebionetworks.amp.als.dataset.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * A dataset
 */

@Schema(name = "DatasetJsonLd", description = "A dataset")
@JsonTypeName("DatasetJsonLd")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class DatasetJsonLdDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  @JsonProperty("@context")
  private String atContext;

  @JsonProperty("@id")
  private String atId;

  @JsonProperty("@type")
  private String atType;

  public DatasetJsonLdDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the dataset.
   * @return id
  */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of the dataset.", required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DatasetJsonLdDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the dataset.
   * @return name
  */
  @NotNull @Size(min = 3, max = 255) 
  @Schema(name = "name", description = "The name of the dataset.", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DatasetJsonLdDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the dataset.
   * @return description
  */
  @NotNull @Size(min = 0, max = 1000) 
  @Schema(name = "description", example = "This is an example description of the dataset.", description = "The description of the dataset.", required = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DatasetJsonLdDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Datetime when the object was added to the database.
   * @return createdAt
  */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was added to the database.", required = true)
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DatasetJsonLdDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Datetime when the object was last modified in the database.
   * @return updatedAt
  */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was last modified in the database.", required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public DatasetJsonLdDto atContext(String atContext) {
    this.atContext = atContext;
    return this;
  }

  /**
   * Get atContext
   * @return atContext
  */
  @NotNull 
  @Schema(name = "@context", example = "https://schema.org", required = true)
  public String getAtContext() {
    return atContext;
  }

  public void setAtContext(String atContext) {
    this.atContext = atContext;
  }

  public DatasetJsonLdDto atId(String atId) {
    this.atId = atId;
    return this;
  }

  /**
   * Get atId
   * @return atId
  */
  @NotNull 
  @Schema(name = "@id", example = "https://example.com/api/v1/datasets/1", required = true)
  public String getAtId() {
    return atId;
  }

  public void setAtId(String atId) {
    this.atId = atId;
  }

  public DatasetJsonLdDto atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * Get atType
   * @return atType
  */
  @NotNull 
  @Schema(name = "@type", example = "Dataset", required = true)
  public String getAtType() {
    return atType;
  }

  public void setAtType(String atType) {
    this.atType = atType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatasetJsonLdDto datasetJsonLd = (DatasetJsonLdDto) o;
    return Objects.equals(this.id, datasetJsonLd.id) &&
        Objects.equals(this.name, datasetJsonLd.name) &&
        Objects.equals(this.description, datasetJsonLd.description) &&
        Objects.equals(this.createdAt, datasetJsonLd.createdAt) &&
        Objects.equals(this.updatedAt, datasetJsonLd.updatedAt) &&
        Objects.equals(this.atContext, datasetJsonLd.atContext) &&
        Objects.equals(this.atId, datasetJsonLd.atId) &&
        Objects.equals(this.atType, datasetJsonLd.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, createdAt, updatedAt, atContext, atId, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatasetJsonLdDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    atContext: ").append(toIndentedString(atContext)).append("\n");
    sb.append("    atId: ").append(toIndentedString(atId)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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
}

