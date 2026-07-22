package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Marmoset model details
 */

@Schema(name = "MarmosetModel", description = "Marmoset model details")
@JsonTypeName("MarmosetModel")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MarmosetModelDto implements ModelDto {

  private String type;

  private String name;

  private String modelType;

  private String studySynid;

  @Valid
  private List<@Valid GeneticInfoDto> geneticInfo = new ArrayList<>();

  @Valid
  private List<@Valid ModelDataDto> biomarkers = new ArrayList<>();

  public MarmosetModelDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MarmosetModelDto(String type, String name, String modelType, String studySynid, List<@Valid GeneticInfoDto> geneticInfo, List<@Valid ModelDataDto> biomarkers) {
    this.type = type;
    this.name = name;
    this.modelType = modelType;
    this.studySynid = studySynid;
    this.geneticInfo = geneticInfo;
    this.biomarkers = biomarkers;
  }

  public MarmosetModelDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Organism discriminator (always \"marmoset\" for this schema)
   * @return type
   */
  @NotNull 
  @Schema(name = "type", description = "Organism discriminator (always \"marmoset\" for this schema)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public MarmosetModelDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the model
   * @return name
   */
  @NotNull 
  @Schema(name = "name", description = "Name of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MarmosetModelDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Type of the model
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", description = "Type of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public MarmosetModelDto studySynid(String studySynid) {
    this.studySynid = studySynid;
    return this;
  }

  /**
   * Synapse ID of the study
   * @return studySynid
   */
  @NotNull 
  @Schema(name = "study_synid", description = "Synapse ID of the study", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("study_synid")
  public String getStudySynid() {
    return studySynid;
  }

  public void setStudySynid(String studySynid) {
    this.studySynid = studySynid;
  }

  public MarmosetModelDto geneticInfo(List<@Valid GeneticInfoDto> geneticInfo) {
    this.geneticInfo = geneticInfo;
    return this;
  }

  public MarmosetModelDto addGeneticInfoItem(GeneticInfoDto geneticInfoItem) {
    if (this.geneticInfo == null) {
      this.geneticInfo = new ArrayList<>();
    }
    this.geneticInfo.add(geneticInfoItem);
    return this;
  }

  /**
   * Genetic information related to the Model
   * @return geneticInfo
   */
  @NotNull @Valid 
  @Schema(name = "genetic_info", description = "Genetic information related to the Model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genetic_info")
  public List<@Valid GeneticInfoDto> getGeneticInfo() {
    return geneticInfo;
  }

  public void setGeneticInfo(List<@Valid GeneticInfoDto> geneticInfo) {
    this.geneticInfo = geneticInfo;
  }

  public MarmosetModelDto biomarkers(List<@Valid ModelDataDto> biomarkers) {
    this.biomarkers = biomarkers;
    return this;
  }

  public MarmosetModelDto addBiomarkersItem(ModelDataDto biomarkersItem) {
    if (this.biomarkers == null) {
      this.biomarkers = new ArrayList<>();
    }
    this.biomarkers.add(biomarkersItem);
    return this;
  }

  /**
   * List of biomarker data associated with the model
   * @return biomarkers
   */
  @NotNull @Valid 
  @Schema(name = "biomarkers", description = "List of biomarker data associated with the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("biomarkers")
  public List<@Valid ModelDataDto> getBiomarkers() {
    return biomarkers;
  }

  public void setBiomarkers(List<@Valid ModelDataDto> biomarkers) {
    this.biomarkers = biomarkers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarmosetModelDto marmosetModel = (MarmosetModelDto) o;
    return Objects.equals(this.type, marmosetModel.type) &&
        Objects.equals(this.name, marmosetModel.name) &&
        Objects.equals(this.modelType, marmosetModel.modelType) &&
        Objects.equals(this.studySynid, marmosetModel.studySynid) &&
        Objects.equals(this.geneticInfo, marmosetModel.geneticInfo) &&
        Objects.equals(this.biomarkers, marmosetModel.biomarkers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, modelType, studySynid, geneticInfo, biomarkers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarmosetModelDto {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    studySynid: ").append(toIndentedString(studySynid)).append("\n");
    sb.append("    geneticInfo: ").append(toIndentedString(geneticInfo)).append("\n");
    sb.append("    biomarkers: ").append(toIndentedString(biomarkers)).append("\n");
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

    private MarmosetModelDto instance;

    public Builder() {
      this(new MarmosetModelDto());
    }

    protected Builder(MarmosetModelDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MarmosetModelDto value) { 
      this.instance.setType(value.type);
      this.instance.setName(value.name);
      this.instance.setModelType(value.modelType);
      this.instance.setStudySynid(value.studySynid);
      this.instance.setGeneticInfo(value.geneticInfo);
      this.instance.setBiomarkers(value.biomarkers);
      return this;
    }

    public MarmosetModelDto.Builder type(String type) {
      this.instance.type(type);
      return this;
    }
    
    public MarmosetModelDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public MarmosetModelDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public MarmosetModelDto.Builder studySynid(String studySynid) {
      this.instance.studySynid(studySynid);
      return this;
    }
    
    public MarmosetModelDto.Builder geneticInfo(List<GeneticInfoDto> geneticInfo) {
      this.instance.geneticInfo(geneticInfo);
      return this;
    }
    
    public MarmosetModelDto.Builder biomarkers(List<ModelDataDto> biomarkers) {
      this.instance.biomarkers(biomarkers);
      return this;
    }
    
    /**
    * returns a built MarmosetModelDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MarmosetModelDto build() {
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
  public static MarmosetModelDto.Builder builder() {
    return new MarmosetModelDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MarmosetModelDto.Builder toBuilder() {
    MarmosetModelDto.Builder builder = new MarmosetModelDto.Builder();
    return builder.copyOf(this);
  }

}

