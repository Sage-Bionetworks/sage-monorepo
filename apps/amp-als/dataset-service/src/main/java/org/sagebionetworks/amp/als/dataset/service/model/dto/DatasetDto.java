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

@Schema(name = "Dataset", description = "A dataset")
@JsonTypeName("Dataset")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class DatasetDto {

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

  public DatasetDto id(Long id) {
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

  public DatasetDto name(String name) {
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

  public DatasetDto description(String description) {
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

  public DatasetDto createdAt(OffsetDateTime createdAt) {
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

  public DatasetDto updatedAt(OffsetDateTime updatedAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatasetDto dataset = (DatasetDto) o;
    return Objects.equals(this.id, dataset.id) &&
        Objects.equals(this.name, dataset.name) &&
        Objects.equals(this.description, dataset.description) &&
        Objects.equals(this.createdAt, dataset.createdAt) &&
        Objects.equals(this.updatedAt, dataset.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatasetDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

