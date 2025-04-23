package org.sagebionetworks.amp.als.dataset.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A dataset
 */

@Schema(name = "Dataset", description = "A dataset")
@JsonTypeName("Dataset")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DatasetDto {

  private Long id;

  private String name;

  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public DatasetDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DatasetDto(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public DatasetDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the dataset.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of the dataset.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
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
  @Schema(name = "name", description = "The name of the dataset.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
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
  @Schema(name = "description", example = "This is an example description of the dataset.", description = "The description of the dataset.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
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
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was added to the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
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
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was last modified in the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
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
  
  public static class Builder {

    private DatasetDto instance;

    public Builder() {
      this(new DatasetDto());
    }

    protected Builder(DatasetDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DatasetDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public DatasetDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public DatasetDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public DatasetDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public DatasetDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public DatasetDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built DatasetDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DatasetDto build() {
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
  public static DatasetDto.Builder builder() {
    return new DatasetDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DatasetDto.Builder toBuilder() {
    DatasetDto.Builder builder = new DatasetDto.Builder();
    return builder.copyOf(this);
  }

}

