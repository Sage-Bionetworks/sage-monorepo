package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.LinkDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Marmoset Model Overview object for comparison tools
 */

@Schema(name = "MarmosetModelOverview", description = "Marmoset Model Overview object for comparison tools")
@JsonTypeName("MarmosetModelOverview")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MarmosetModelOverviewDto {

  private String id;

  private String name;

  private String modelType;

  private @Nullable LinkDto biomarkers;

  private LinkDto studyData;

  @Valid
  private List<String> modifiedGenes = new ArrayList<>();

  /**
   * Gets or Sets availableData
   */
  public enum AvailableDataEnum {
    PLASMA_BIOMARKERS("Plasma Biomarkers");

    private final String value;

    AvailableDataEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AvailableDataEnum fromValue(String value) {
      for (AvailableDataEnum b : AvailableDataEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private List<AvailableDataEnum> availableData = new ArrayList<>();

  public MarmosetModelOverviewDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MarmosetModelOverviewDto(String id, String name, String modelType, LinkDto studyData, List<String> modifiedGenes, List<AvailableDataEnum> availableData) {
    this.id = id;
    this.name = name;
    this.modelType = modelType;
    this.studyData = studyData;
    this.modifiedGenes = modifiedGenes;
    this.availableData = availableData;
  }

  public MarmosetModelOverviewDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the marmoset model overview object
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", description = "Unique identifier for the marmoset model overview object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MarmosetModelOverviewDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Model name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", description = "Model name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MarmosetModelOverviewDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Model type (e.g., Late Onset AD, Familial AD)
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", description = "Model type (e.g., Late Onset AD, Familial AD)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public MarmosetModelOverviewDto biomarkers(@Nullable LinkDto biomarkers) {
    this.biomarkers = biomarkers;
    return this;
  }

  /**
   * Get biomarkers
   * @return biomarkers
   */
  @Valid 
  @Schema(name = "biomarkers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("biomarkers")
  public @Nullable LinkDto getBiomarkers() {
    return biomarkers;
  }

  public void setBiomarkers(@Nullable LinkDto biomarkers) {
    this.biomarkers = biomarkers;
  }

  public MarmosetModelOverviewDto studyData(LinkDto studyData) {
    this.studyData = studyData;
    return this;
  }

  /**
   * Get studyData
   * @return studyData
   */
  @NotNull @Valid 
  @Schema(name = "study_data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("study_data")
  public LinkDto getStudyData() {
    return studyData;
  }

  public void setStudyData(LinkDto studyData) {
    this.studyData = studyData;
  }

  public MarmosetModelOverviewDto modifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public MarmosetModelOverviewDto addModifiedGenesItem(String modifiedGenesItem) {
    if (this.modifiedGenes == null) {
      this.modifiedGenes = new ArrayList<>();
    }
    this.modifiedGenes.add(modifiedGenesItem);
    return this;
  }

  /**
   * List of modified genes in the model
   * @return modifiedGenes
   */
  @NotNull 
  @Schema(name = "modified_genes", description = "List of modified genes in the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modified_genes")
  public List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public MarmosetModelOverviewDto availableData(List<AvailableDataEnum> availableData) {
    this.availableData = availableData;
    return this;
  }

  public MarmosetModelOverviewDto addAvailableDataItem(AvailableDataEnum availableDataItem) {
    if (this.availableData == null) {
      this.availableData = new ArrayList<>();
    }
    this.availableData.add(availableDataItem);
    return this;
  }

  /**
   * Get availableData
   * @return availableData
   */
  @NotNull 
  @Schema(name = "available_data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("available_data")
  public List<AvailableDataEnum> getAvailableData() {
    return availableData;
  }

  public void setAvailableData(List<AvailableDataEnum> availableData) {
    this.availableData = availableData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarmosetModelOverviewDto marmosetModelOverview = (MarmosetModelOverviewDto) o;
    return Objects.equals(this.id, marmosetModelOverview.id) &&
        Objects.equals(this.name, marmosetModelOverview.name) &&
        Objects.equals(this.modelType, marmosetModelOverview.modelType) &&
        Objects.equals(this.biomarkers, marmosetModelOverview.biomarkers) &&
        Objects.equals(this.studyData, marmosetModelOverview.studyData) &&
        Objects.equals(this.modifiedGenes, marmosetModelOverview.modifiedGenes) &&
        Objects.equals(this.availableData, marmosetModelOverview.availableData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, modelType, biomarkers, studyData, modifiedGenes, availableData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarmosetModelOverviewDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    biomarkers: ").append(toIndentedString(biomarkers)).append("\n");
    sb.append("    studyData: ").append(toIndentedString(studyData)).append("\n");
    sb.append("    modifiedGenes: ").append(toIndentedString(modifiedGenes)).append("\n");
    sb.append("    availableData: ").append(toIndentedString(availableData)).append("\n");
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

    private MarmosetModelOverviewDto instance;

    public Builder() {
      this(new MarmosetModelOverviewDto());
    }

    protected Builder(MarmosetModelOverviewDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MarmosetModelOverviewDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setModelType(value.modelType);
      this.instance.setBiomarkers(value.biomarkers);
      this.instance.setStudyData(value.studyData);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setAvailableData(value.availableData);
      return this;
    }

    public MarmosetModelOverviewDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder biomarkers(LinkDto biomarkers) {
      this.instance.biomarkers(biomarkers);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder studyData(LinkDto studyData) {
      this.instance.studyData(studyData);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public MarmosetModelOverviewDto.Builder availableData(List<AvailableDataEnum> availableData) {
      this.instance.availableData(availableData);
      return this;
    }
    
    /**
    * returns a built MarmosetModelOverviewDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MarmosetModelOverviewDto build() {
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
  public static MarmosetModelOverviewDto.Builder builder() {
    return new MarmosetModelOverviewDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MarmosetModelOverviewDto.Builder toBuilder() {
    MarmosetModelOverviewDto.Builder builder = new MarmosetModelOverviewDto.Builder();
    return builder.copyOf(this);
  }

}

